package com.capstone.dfms.services;

import com.capstone.dfms.models.ExportItemEntity;

import java.util.List;

public interface IExportItemService {

    ExportItemEntity createExportItem(ExportItemEntity exportItem);

    ExportItemEntity getExportItemById(long id);

    List<ExportItemEntity> getAllExportItems();

    ExportItemEntity approveExportItem (Long id);

    ExportItemEntity rejectExportItem (Long id);

    ExportItemEntity cancelExportItem (Long id);

    ExportItemEntity updateExportItem (Long id, float quantiy);

    ExportItemEntity exportItem (Long id);

}
