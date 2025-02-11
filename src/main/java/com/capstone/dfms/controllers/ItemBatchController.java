package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.models.enums.BatchStatus;
import com.capstone.dfms.requests.ItemBatchRequest;
import com.capstone.dfms.services.IItemBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IItemBatchMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/itembatchs")
@RequiredArgsConstructor
public class ItemBatchController {
    private final IItemBatchService itemBatchService;

    @PostMapping("/create")
    public CoreApiResponse<?> createItemBatch(
            @Valid @RequestBody ItemBatchRequest request
    ){
        itemBatchService.createItemBatch(INSTANCE.toModel(request));
        return CoreApiResponse.success("Create item batch successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<ItemBatchEntity>> getAll() {
        return CoreApiResponse.success(itemBatchService.getAllItemBatchs());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<ItemBatchEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(itemBatchService.getItemBatchById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteItemBatch(
            @PathVariable Long id
    ){
        itemBatchService.deleteItemBatch(id);
        return CoreApiResponse.success("Delete item batch successfully");
    }

    @PutMapping("update/{id}/{status}")
    public CoreApiResponse<?> updateItemBatch(
            @PathVariable Long id,
            @PathVariable BatchStatus status) {
        itemBatchService.updateItemBatch(id, status);
        return CoreApiResponse.success("Item batch updated successfully.");
    }
}
