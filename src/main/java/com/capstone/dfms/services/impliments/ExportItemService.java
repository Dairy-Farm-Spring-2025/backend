package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ExportItemEntity;
import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.BatchStatus;
import com.capstone.dfms.models.enums.ExportItemStatus;
import com.capstone.dfms.repositories.IExportItemRepository;
import com.capstone.dfms.repositories.IItemBatchRepository;
import com.capstone.dfms.services.IExportItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportItemService implements IExportItemService {
    private final IExportItemRepository exportItemRepository;

    private final IItemBatchRepository itemBatchRepository;


    @Override
    public ExportItemEntity createExportItem(ExportItemEntity exportItem) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        ItemBatchEntity itemBatch = itemBatchRepository.findById(exportItem.getItemBatchEntity().getItemBatchId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.not.exist")));

        if (exportItem.getQuantity() > itemBatch.getQuantity()) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.quantity.exceed"));
        }

        exportItem.setPicker(user);
        exportItem.setStatus(ExportItemStatus.pending);
        exportItem.setExportDate(LocalDateTime.now());
        exportItem.setItemBatchEntity(itemBatch);

        return exportItemRepository.save(exportItem);
    }

    @Override
    public ExportItemEntity approveExportItem (Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        ExportItemEntity exportItem = exportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.not.exist")));

        exportItem.setStatus(ExportItemStatus.approved);
        exportItem.setExporter(user);

        return exportItemRepository.save(exportItem);
    }

    @Override
    public List<ExportItemEntity> approveMultipleExportItems(List<Long> listId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        List<ExportItemEntity> exportItems = exportItemRepository.findAllById(listId);

        if (exportItems.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.no.valid.items"));
        }

        for (ExportItemEntity exportItem : exportItems) {
            if (exportItem.getStatus() != ExportItemStatus.pending) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.approve.invalid.status"));
            }
            exportItem.setStatus(ExportItemStatus.approved);
            exportItem.setExporter(user);
        }

        return exportItemRepository.saveAll(exportItems);
    }


    @Override
    public ExportItemEntity rejectExportItem (Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        ExportItemEntity exportItem = exportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.not.exist")));

        exportItem.setStatus(ExportItemStatus.reject);
        exportItem.setExporter(user);

        return exportItemRepository.save(exportItem);
    }


    @Override
    public ExportItemEntity cancelExportItem (Long id){
        ExportItemEntity exportItem = exportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This export item is not existed!"));
       if (exportItem.getStatus() != ExportItemStatus.pending) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.cancel.invalid.status"));
        }

        exportItem.setStatus(ExportItemStatus.cancel);

        return exportItemRepository.save(exportItem);
    }

    @Override
    public ExportItemEntity updateExportItem(Long id, float quantiy) {
        ExportItemEntity exportItem = exportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This export item is not existed!"));
        if (exportItem.getStatus() != ExportItemStatus.pending) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.update.invalid.status"));
        }
        exportItem.setQuantity(quantiy);

        return exportItemRepository.save(exportItem);
    }

    @Override
    public ExportItemEntity exportItem(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        ExportItemEntity exportItem = exportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.not.exist")));

        if (!exportItem.getPicker().getId().equals(user.getId())) {
            throw new AppException(HttpStatus.FORBIDDEN, LocalizationUtils.getMessage("export.item.not.authorized"));
        }
        ItemBatchEntity itemBatch = exportItem.getItemBatchEntity();
        float exportQuantity = exportItem.getQuantity();

        if (itemBatch.getQuantity() < exportQuantity) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.batch.not.enough"));
        }

        itemBatch.setQuantity(itemBatch.getQuantity() - exportQuantity);

        if (itemBatch.getQuantity() == 0) {
            itemBatch.setStatus(BatchStatus.depleted);
        }
        exportItem.setStatus(ExportItemStatus.exported);
        exportItem.setExportDate(LocalDateTime.now());

        itemBatchRepository.save(itemBatch);
        return exportItemRepository.save(exportItem);
    }




    @Override
    public ExportItemEntity getExportItemById(long id) {
        return exportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.not.exist")));
    }

    @Override
    public List<ExportItemEntity> getAllExportItems() {
        return exportItemRepository.findAll();
    }

}
