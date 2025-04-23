package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.CategoryEntity;
import com.capstone.dfms.requests.CategoryRequest;
import com.capstone.dfms.services.ICategoryServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${app.api.version.v1}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryServices categoryServices;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createCategory(
            @Valid @RequestBody CategoryRequest request
    ){
        categoryServices.createCategory(request.getName());
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.create_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<CategoryEntity>> getAll() {
        return CoreApiResponse.success(categoryServices.getAllCategorys());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<CategoryEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(categoryServices.getCategoryById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteCategory(
            @PathVariable Long id
    ){
        categoryServices.deleteCategory(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.delete_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<?> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        categoryServices.updateCategory(id,request.getName());
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.update_successfully"));
    }
}
