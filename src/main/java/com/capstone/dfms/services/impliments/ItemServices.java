package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.ItemMapper;
import com.capstone.dfms.models.CategoryEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.repositories.ICategoryRepository;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.repositories.IWarehouseLocationRepository;
import com.capstone.dfms.requests.ItemCreateRequest;
import com.capstone.dfms.services.IItemServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServices implements IItemServices {
    private final IItemRepository itemRepository;

    private final ICategoryRepository categoryRepository;

    private final IWarehouseLocationRepository locationRepository;

    private final ItemMapper itemMapper;
    @Override
    public ItemEntity createItem(ItemEntity itemEntity) {
        CategoryEntity category = categoryRepository.findById(itemEntity.getCategoryEntity().getCategoryId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, LocalizationUtils.getMessage("category.not_exist")));
        itemEntity.setCategoryEntity(category);

        WarehouseLocationEntity warehouseLocation =
                locationRepository.findById(itemEntity.getWarehouseLocationEntity().getWarehouseLocationId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, LocalizationUtils.getMessage("warehouse.not_exist")));
        itemEntity.setWarehouseLocationEntity(warehouseLocation);

        return itemRepository.save(itemEntity);
    }

    @Override
    public ItemEntity getItemById(long id) {
         return itemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_exist")));
    }

    @Override
    public List<ItemEntity> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public ItemEntity updateItem(Long id, ItemCreateRequest request) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_exist")));
        itemMapper.updateItemFromRequest(request, itemEntity);

        if (request.getLocationId() != null) {
            WarehouseLocationEntity locationEntity = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("warehouse.not_exist")));
            itemEntity.setWarehouseLocationEntity(locationEntity);
        }

        if (request.getCategoryId() != null) {
            CategoryEntity categoryEntity = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new  AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("category.not_exist")));
            itemEntity.setCategoryEntity(categoryEntity);
        }
        return itemRepository.save(itemEntity);
    }

    @Override
    public void deleteItem(long id) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new  AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_exist")));
        itemRepository.delete(itemEntity);

    }

    @Override
    public List<ItemEntity> getItemsByCategoryId(Long categoryId) {
        return itemRepository.findItemsByCategoryId(categoryId);
    }

    @Override
    public List<ItemEntity> getItemsByLocationId(Long locationId) {
        return itemRepository.findItemsByLocationId(locationId);
    }

    @Override
    public List<ItemEntity> getItemsVaccine() {
        return itemRepository.findItemsByCategoryName("Vắc-xin");
    }
}
