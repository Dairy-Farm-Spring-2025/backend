package com.capstone.dfms.services;

import com.capstone.dfms.models.CategoryEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.requests.ItemCreateRequest;

import java.util.List;

public interface IItemServices {
    ItemEntity createItem(ItemEntity itemEntity);

    ItemEntity getItemById(long id);

    List<ItemEntity> getAllItems();

    ItemEntity updateItem(Long id, ItemCreateRequest request );

    void deleteItem(long id);

    List<ItemEntity> getItemsByCategoryId(Long categoryId);

    List<ItemEntity> getItemsByLocationId(Long locationId);
}
