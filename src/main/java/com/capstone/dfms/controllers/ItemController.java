package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.requests.ItemCreateRequest;
import com.capstone.dfms.requests.WarehouseUpdateRequest;
import com.capstone.dfms.services.IItemServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.ItemMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/items")
@RequiredArgsConstructor
public class ItemController {
    private final IItemServices itemServices;

    @PostMapping("/create")
    public CoreApiResponse<?> createItem(
            @Valid @RequestBody ItemCreateRequest request
    ){
        itemServices.createItem(INSTANCE.toModel(request));
        return CoreApiResponse.success("Create item successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<ItemEntity>> getAll() {
        return CoreApiResponse.success(itemServices.getAllItems());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<ItemEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(itemServices.getItemById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteItem(
            @PathVariable Long id
    ){
        itemServices.deleteItem(id);
        return CoreApiResponse.success("Delete item successfully");
    }

    @PutMapping("/{id}")
    public CoreApiResponse<?> updateItem(
            @PathVariable Long id,
            @RequestBody ItemCreateRequest request) {
        itemServices.updateItem(id,request );
        return CoreApiResponse.success("Item updated successfully.");
    }

    @GetMapping("/category/{categoryId}")
    public CoreApiResponse<List<ItemEntity>> getItemsByCategory(@PathVariable Long categoryId) {
        return CoreApiResponse.success(itemServices.getItemsByCategoryId(categoryId));
    }

    @GetMapping("/location/{locationId}")
    public CoreApiResponse<List<ItemEntity>> getItemsByLocation(@PathVariable Long locationId) {
        return CoreApiResponse.success(itemServices.getItemsByLocationId(locationId));
    }
}
