package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
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
        exportItemService.createExportItem(INSTANCE.toModel(request));
        return CoreApiResponse.success("Create export item successfully.");
    }
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("approve/{id}")
    public CoreApiResponse<ExportItemEntity> approveExportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.approveExportItem(id));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("reject/{id}")
    public CoreApiResponse<ExportItemEntity> rejectExportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.rejectExportItem(id));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("cancel/{id}")
    public CoreApiResponse<ExportItemEntity> cancelExportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.cancelExportItem(id));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("export/{id}")
    public CoreApiResponse<ExportItemEntity> exportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.exportItem(id));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("update/{id}/{quantity}")
    public CoreApiResponse<ExportItemEntity> updateExportItem(@PathVariable Long id, @PathVariable float quantity) {
        return CoreApiResponse.success(exportItemService.updateExportItem(id,quantity));
    }

    @GetMapping
    public CoreApiResponse<List<ExportItemEntity>> getAll() {
        return CoreApiResponse.success(exportItemService.getAllExportItems());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<ExportItemEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.getExportItemById(id));
    }


}
