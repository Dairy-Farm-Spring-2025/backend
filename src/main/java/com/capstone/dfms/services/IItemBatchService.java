package com.capstone.dfms.services;

import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.models.enums.BatchStatus;


import java.util.List;

public interface IItemBatchService {

    ItemBatchEntity createItemBatch(ItemBatchEntity itemBatch);

    ItemBatchEntity getItemBatchById(long id);

    List<ItemBatchEntity> getAllItemBatchs();

    ItemBatchEntity updateItemBatch(Long id, BatchStatus status);

    void deleteItemBatch(long id);

    List<ItemBatchEntity> getItemBatchesByItemId(Long itemId);
}
