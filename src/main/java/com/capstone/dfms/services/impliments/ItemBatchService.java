package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.mappers.IItemBatchMapper;
import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.SupplierEntity;
import com.capstone.dfms.models.enums.BatchStatus;
import com.capstone.dfms.repositories.IItemBatchRepository;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.repositories.ISupplierRepository;
import com.capstone.dfms.requests.ItemBatchRequest;
import com.capstone.dfms.services.IItemBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemBatchService implements IItemBatchService {
    private final IItemBatchRepository itemBatchRepository;

    private final IItemRepository itemRepository;

    private final ISupplierRepository supplierRepository;

    private final IItemBatchMapper itemBatchMapper;

    @Override
    public ItemBatchEntity createItemBatch(ItemBatchEntity itemBatch) {
        ItemEntity item = itemRepository.findById(itemBatch.getItemEntity().getItemId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Item not found."));

        SupplierEntity supplier = supplierRepository.findById(itemBatch.getSupplierEntity().getSupplierId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Supplier not found."));

        if (itemBatch.getExpiryDate() != null && itemBatch.getExpiryDate().isBefore(LocalDate.now())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Expiry date must be in the future.");
        }

        itemBatch.setItemEntity(item);
        itemBatch.setSupplierEntity(supplier);
        itemBatch.setImportDate(LocalDate.now());
        itemBatch.setStatus(BatchStatus.available);
        return itemBatchRepository.save(itemBatch);
    }

    @Override
    public ItemBatchEntity getItemBatchById(long id) {
        return itemBatchRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This item batch is not existed!"));
    }

    @Override
    public List<ItemBatchEntity> getAllItemBatchs() {
        return itemBatchRepository.findAll();
    }

    @Override
    public ItemBatchEntity updateItemBatch(Long id, ItemBatchRequest request) {
        ItemBatchEntity itemBatch = itemBatchRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Item batch", "id", id));

        itemBatchMapper.updateItemBatchFromRequest(request, itemBatch);



        return itemBatchRepository.save(itemBatch);
    }

    @Override
    public void deleteItemBatch(long id) {
        ItemBatchEntity itemBatch = itemBatchRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Item batch", "id", id));

        itemBatchRepository.delete(itemBatch);
    }
}
