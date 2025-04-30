package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.requests.ItemCreateRequest;
import com.capstone.dfms.services.IItemServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.ItemMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/items")
@RequiredArgsConstructor
public class ItemController {
    private final IItemServices itemServices;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createItem(
            @Valid @RequestBody ItemCreateRequest request
    ){
        itemServices.createItem(INSTANCE.toModel(request));
        return CoreApiResponse.success(LocalizationUtils.getMessage("item.create.success"));
    }
    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<ItemEntity>> getAll() {
        return CoreApiResponse.success(itemServices.getAllItems());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<ItemEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(itemServices.getItemById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteItem(
            @PathVariable Long id
    ){
        itemServices.deleteItem(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("item.delete.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<?> updateItem(
            @PathVariable Long id,
            @RequestBody ItemCreateRequest request) {
        itemServices.updateItem(id,request );
        return CoreApiResponse.success(LocalizationUtils.getMessage("item.update.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/category/{categoryId}")
    public CoreApiResponse<List<ItemEntity>> getItemsByCategory(@PathVariable Long categoryId) {
        return CoreApiResponse.success(itemServices.getItemsByCategoryId(categoryId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/location/{locationId}")
    public CoreApiResponse<List<ItemEntity>> getItemsByLocation(@PathVariable Long locationId) {
        return CoreApiResponse.success(itemServices.getItemsByLocationId(locationId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/vaccine")
    public CoreApiResponse<List<ItemEntity>> getItemsVaccine() {
        return CoreApiResponse.success(itemServices.getItemsVaccine());
    }

    @GetMapping("/check-stock/{itemId}")
    public CoreApiResponse<String> checkItemStock(@PathVariable Long itemId) {
        String message = itemServices.checkLowStockByItemId(itemId);
        return CoreApiResponse.success(message);
    }
}
