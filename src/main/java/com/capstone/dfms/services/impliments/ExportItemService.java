package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.BatchStatus;
import com.capstone.dfms.models.enums.ExportItemStatus;
import com.capstone.dfms.repositories.IExportItemRepository;
import com.capstone.dfms.repositories.IItemBatchRepository;
import com.capstone.dfms.repositories.ITaskRepository;
import com.capstone.dfms.requests.CreateExportItemsRequest;
import com.capstone.dfms.requests.ExportItemDetailRequest;
import com.capstone.dfms.requests.ExportItemRequest;
import com.capstone.dfms.services.IExportItemService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportItemService implements IExportItemService {
    private final IExportItemRepository exportItemRepository;

    private final IItemBatchRepository itemBatchRepository;

    private final ITaskRepository taskRepository;


    @Override
    public void createExportItem(ExportItemRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        TaskEntity task = taskRepository.findById(request.getTaskId()).orElseThrow(() ->
                new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.not.found")));

        List<ItemBatchEntity> inUseBatches = itemBatchRepository.findByItemEntity_ItemIdAndStatusOrderByImportDateAsc(
                request.getItemId(), BatchStatus.inUse);

        List<ItemBatchEntity> availableBatches = itemBatchRepository.findByItemEntity_ItemIdAndStatusOrderByImportDateAsc(
                request.getItemId(), BatchStatus.available);

        float totalAvailableQuantity = 0;

        for (ItemBatchEntity batch : inUseBatches) {
            totalAvailableQuantity += batch.getQuantity();
        }

        for (ItemBatchEntity batch : availableBatches) {
            totalAvailableQuantity += batch.getQuantity();
        }

        if (totalAvailableQuantity < request.getQuantity()) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.quantity.exceed"));
        }

        float remainingQuantity = request.getQuantity();

        for (ItemBatchEntity batch : inUseBatches) {
            if (remainingQuantity <= 0) break;

            float availableQuantity = batch.getQuantity();
            float exportQuantity = Math.min(availableQuantity, remainingQuantity);

            batch.setQuantity(batch.getQuantity() - exportQuantity);
            if (batch.getQuantity() == 0) {
                batch.setStatus(BatchStatus.depleted);
            }
            itemBatchRepository.save(batch);

            ExportItemEntity exportItem = new ExportItemEntity();
            exportItem.setPicker(user);
            exportItem.setExportDate(LocalDateTime.now());
            exportItem.setStatus(ExportItemStatus.pending);
            exportItem.setItemBatchEntity(batch);
            exportItem.setQuantity(exportQuantity);
            exportItem.setTask(task);

            exportItemRepository.save(exportItem);

            remainingQuantity -= exportQuantity;
        }

        if (remainingQuantity > 0) {
            for (ItemBatchEntity batch : availableBatches) {
                if (remainingQuantity <= 0) break;

                float availableQuantity = batch.getQuantity();
                float exportQuantity = Math.min(availableQuantity, remainingQuantity);

                batch.setQuantity(batch.getQuantity() - exportQuantity);
                if (batch.getQuantity() == 0) {
                    batch.setStatus(BatchStatus.depleted);
                } else {
                    batch.setStatus(BatchStatus.inUse);
                }
                itemBatchRepository.save(batch);
                ExportItemEntity exportItem2 = new ExportItemEntity();
                exportItem2.setPicker(user);
                exportItem2.setExportDate(LocalDateTime.now());
                exportItem2.setStatus(ExportItemStatus.pending);
                exportItem2.setItemBatchEntity(batch);
                exportItem2.setQuantity(exportQuantity);
                exportItem2.setTask(task);
                exportItemRepository.save(exportItem2);

                remainingQuantity -= exportQuantity;
            }
        }
    }

    @Override
    public ExportItemEntity cancelExportItem(Long id) {
        ExportItemEntity exportItem = exportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This export item is not existed!"));

        if (exportItem.getStatus() != ExportItemStatus.pending) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.cancel.invalid.status"));
        }

        ItemBatchEntity itemBatch = exportItem.getItemBatchEntity();
        itemBatch.setQuantity(itemBatch.getQuantity() + exportItem.getQuantity());

        if (itemBatch.getStatus() == BatchStatus.depleted) {
            itemBatch.setStatus(BatchStatus.inUse);
        }

        itemBatchRepository.save(itemBatch);

        exportItem.setStatus(ExportItemStatus.cancel);
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

        exportItem.setStatus(ExportItemStatus.exported);
        exportItem.setExportDate(LocalDateTime.now());

        return exportItemRepository.save(exportItem);
    }

    @Override
    public List<ExportItemEntity> exportItems(List<Long> ids) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        List<ExportItemEntity> exportItems = exportItemRepository.findAllById(ids);

        if (exportItems.size() != ids.size()) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.not.exist"));
        }

        for (ExportItemEntity item : exportItems) {
            if (!item.getPicker().getId().equals(user.getId())) {
                throw new AppException(HttpStatus.FORBIDDEN, LocalizationUtils.getMessage("export.item.not.authorized"));
            }

            item.setStatus(ExportItemStatus.exported);
            item.setExportDate(LocalDateTime.now());
        }

        return exportItemRepository.saveAll(exportItems);
    }



    @Override
    public List<ExportItemEntity> getMyExportItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        return exportItemRepository.findByPicker_Id(user.getId());
    }



    @Override
    public ExportItemEntity getExportItemById(long id) {
        return exportItemRepository.findById(id)
                .orElseThrow(
                        () -> new AppException(HttpStatus.BAD_REQUEST,
                                LocalizationUtils.getMessage("export.item.not.exist")));
    }

    @Override
    public List<ExportItemEntity> getAllExportItems() {
        return exportItemRepository.findAll();
    }


    @Override
    @Transactional
    public void createExportItems(CreateExportItemsRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        TaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Task not found"));

        AreaEntity area = task.getAreaId();

        for (ExportItemDetailRequest itemRequest : request.getExportItems()) {
            Long itemId = itemRequest.getItemId();

            if (task.getMainIllness() == null && task.getIllness() == null && task.getVaccineInjection() == null) {
                boolean alreadyExportedToday = exportItemRepository.existsTodayByItemIdAndAreaId(itemId, area.getAreaId());

                if (alreadyExportedToday) {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Item đã được export trong khu vực này hôm nay rồi.");
                }
            }
            List<ItemBatchEntity> inUseBatches = itemBatchRepository.findByItemEntity_ItemIdAndStatusOrderByImportDateAsc(
                    itemRequest.getItemId(), BatchStatus.inUse);

            List<ItemBatchEntity> availableBatches = itemBatchRepository.findByItemEntity_ItemIdAndStatusOrderByImportDateAsc(
                    itemRequest.getItemId(), BatchStatus.available);

            float totalAvailableQuantity = 0;

            for (ItemBatchEntity batch : inUseBatches) {
                totalAvailableQuantity += batch.getQuantity();
            }

            for (ItemBatchEntity batch : availableBatches) {
                totalAvailableQuantity += batch.getQuantity();
            }

            if (totalAvailableQuantity < itemRequest.getQuantity()) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("export.item.quantity.exceed"));
            }

            float remainingQuantity = itemRequest.getQuantity();

            for (ItemBatchEntity batch : inUseBatches) {
                if (remainingQuantity <= 0) break;

                float availableQuantity = batch.getQuantity();
                float exportQuantity = Math.min(availableQuantity, remainingQuantity);

                batch.setQuantity(batch.getQuantity() - exportQuantity);
                if (batch.getQuantity() == 0) {
                    batch.setStatus(BatchStatus.depleted);
                }
                itemBatchRepository.save(batch);

                ExportItemEntity exportItem = ExportItemEntity.builder()
                        .picker(user)
                        .exportDate(LocalDateTime.now())
                        .status(ExportItemStatus.pending)
                        .itemBatchEntity(batch)
                        .quantity(exportQuantity)
                        .task(task)
                        .build();

                exportItemRepository.save(exportItem);

                remainingQuantity -= exportQuantity;
            }

            if (remainingQuantity > 0) {
                for (ItemBatchEntity batch : availableBatches) {
                    if (remainingQuantity <= 0) break;

                    float availableQuantity = batch.getQuantity();
                    float exportQuantity = Math.min(availableQuantity, remainingQuantity);

                    batch.setQuantity(batch.getQuantity() - exportQuantity);
                    if (batch.getQuantity() == 0) {
                        batch.setStatus(BatchStatus.depleted);
                    } else {
                        batch.setStatus(BatchStatus.inUse);
                    }
                    itemBatchRepository.save(batch);

                    ExportItemEntity exportItem = ExportItemEntity.builder()
                            .picker(user)
                            .exportDate(LocalDateTime.now())
                            .status(ExportItemStatus.pending)
                            .itemBatchEntity(batch)
                            .quantity(exportQuantity)
                            .task(task)
                            .build();

                    exportItemRepository.save(exportItem);

                    remainingQuantity -= exportQuantity;
                }
            }
        }
    }
}
