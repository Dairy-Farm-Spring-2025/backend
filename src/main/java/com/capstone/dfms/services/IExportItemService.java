package com.capstone.dfms.services;

import com.capstone.dfms.models.ExportItemEntity;
import com.capstone.dfms.requests.CreateExportItemsRequest;
import com.capstone.dfms.requests.ExportItemRequest;

import java.util.List;

public interface IExportItemService {

    void createExportItem(ExportItemRequest request);

    ExportItemEntity getExportItemById(long id);

    List<ExportItemEntity> getAllExportItems();

//    ExportItemEntity approveExportItem (Long id);

//    List<ExportItemEntity> approveMultipleExportItems(List<Long> listId);

//    ExportItemEntity rejectExportItem (Long id);

    ExportItemEntity cancelExportItem (Long id);

//    ExportItemEntity updateExportItem (Long id, float quantiy);

    List<ExportItemEntity> getMyExportItems();
    ExportItemEntity exportItem (Long id);

    void createExportItems(CreateExportItemsRequest request);

}
