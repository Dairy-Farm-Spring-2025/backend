package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.CategoryEntity;
import com.capstone.dfms.requests.CategoryRequest;
import com.capstone.dfms.services.ICategoryServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${app.api.version.v1}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryServices categoryServices;

    @PostMapping("/create")
    public CoreApiResponse<?> createCategory(
            @Valid @RequestBody CategoryRequest request
    ){
        categoryServices.createCategory(request.getName());
        return CoreApiResponse.success("Create category successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<CategoryEntity>> getAll() {
        return CoreApiResponse.success(categoryServices.getAllCategorys());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<CategoryEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(categoryServices.getCategoryById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteCategory(
            @PathVariable Long id
    ){
        categoryServices.deleteCategory(id);
        return CoreApiResponse.success("Delete category successfully");
    }

    @PutMapping("/{id}")
    public CoreApiResponse<?> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        categoryServices.updateCategory(id,request.getName());
        return CoreApiResponse.success("Category updated successfully.");
    }
}
