package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.models.enums.BatchStatus;
import com.capstone.dfms.requests.ItemBatchRequest;
import com.capstone.dfms.services.IItemBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IItemBatchMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/itembatchs")
@RequiredArgsConstructor
public class ItemBatchController {
    private final IItemBatchService itemBatchService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createItemBatch(
            @Valid @RequestBody ItemBatchRequest request
    ){
        itemBatchService.createItemBatch(INSTANCE.toModel(request));
        return CoreApiResponse.success(LocalizationUtils.getMessage("item_batch.create.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<ItemBatchEntity>> getAll() {
        return CoreApiResponse.success(itemBatchService.getAllItemBatchs());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<ItemBatchEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(itemBatchService.getItemBatchById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteItemBatch(
            @PathVariable Long id
    ){
        itemBatchService.deleteItemBatch(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("item_batch.delete.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("update/{id}/{status}")
    public CoreApiResponse<?> updateItemBatch(
            @PathVariable Long id,
            @PathVariable BatchStatus status) {
        itemBatchService.updateItemBatch(id, status);
        return CoreApiResponse.success(LocalizationUtils.getMessage("item_batch.update.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/batches/{itemId}")
    public CoreApiResponse<List<ItemBatchEntity>> getItemBatchesByItemId(@PathVariable Long itemId) {
            List<ItemBatchEntity> itemBatches = itemBatchService.getItemBatchesByItemId(itemId);
            return CoreApiResponse.success(itemBatches);

    }
}
