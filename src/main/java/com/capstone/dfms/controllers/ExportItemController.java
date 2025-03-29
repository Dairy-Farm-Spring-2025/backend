package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ExportItemEntity;
import com.capstone.dfms.requests.ExportItemRequest;
import com.capstone.dfms.services.IExportItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IExportItemMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/export_items")
@RequiredArgsConstructor
public class ExportItemController {
    private final IExportItemService exportItemService;

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping("/create")
    public CoreApiResponse<?> createExportItem(
            @Valid @RequestBody ExportItemRequest request
    ){
        exportItemService.createExportItem(request);
        return CoreApiResponse.success(LocalizationUtils.getMessage("export.item.create.success"));
    }
//    @PreAuthorize("hasRole('MANAGER')")
//    @PutMapping("approve/{id}")
//    public CoreApiResponse<?> approveExportItem(@PathVariable Long id) {
//        return CoreApiResponse.success(exportItemService.approveExportItem(id),LocalizationUtils.getMessage("export.item.approve.success"));
//    }
//
//    @PreAuthorize("hasRole('MANAGER')")
//    @PutMapping("reject/{id}")
//    public CoreApiResponse<?> rejectExportItem(@PathVariable Long id) {
//        return CoreApiResponse.success(exportItemService.rejectExportItem(id),LocalizationUtils.getMessage("export.item.reject.success"));
//    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("cancel/{id}")
    public CoreApiResponse<?> cancelExportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.cancelExportItem(id),LocalizationUtils.getMessage("export.item.cancel.success"));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("export/{id}")
    public CoreApiResponse<?> exportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.exportItem(id),LocalizationUtils.getMessage("export.item.confirm.success"));
    }


//    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
//    @PutMapping("/approves")
//    public CoreApiResponse<?> approveMultipleExportItems(@RequestBody List<Long> ids) {
//        List<ExportItemEntity> approvedItems = exportItemService.approveMultipleExportItems(ids);
//        return CoreApiResponse.success(LocalizationUtils.getMessage("export.item.approve.multiple.success"));
//    }
//
//    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
//    @PutMapping("update/{id}/{quantity}")
//    public CoreApiResponse<?> updateExportItem(@PathVariable Long id, @PathVariable float quantity) {
//        return CoreApiResponse.success(exportItemService.updateExportItem(id,quantity),LocalizationUtils.getMessage("export.item.update.success"));
//    }

    @GetMapping
    public CoreApiResponse<List<ExportItemEntity>> getAll() {
        return CoreApiResponse.success(exportItemService.getAllExportItems());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<ExportItemEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.getExportItemById(id));
    }
}
