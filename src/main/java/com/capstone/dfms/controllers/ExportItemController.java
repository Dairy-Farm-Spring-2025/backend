package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ExportItemEntity;
import com.capstone.dfms.requests.CreateExportItemsRequest;
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

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createExportItem(
            @Valid @RequestBody ExportItemRequest request
    ){
        exportItemService.createExportItem(request);
        return CoreApiResponse.success(LocalizationUtils.getMessage("export.item.create.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @PutMapping("cancel/{id}")
    public CoreApiResponse<?> cancelExportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.cancelExportItem(id),LocalizationUtils.getMessage("export.item.cancel.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @PutMapping("export/{id}")
    public CoreApiResponse<?> exportItem(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.exportItem(id),LocalizationUtils.getMessage("export.item.confirm.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<ExportItemEntity>> getAll() {
        return CoreApiResponse.success(exportItemService.getAllExportItems());
    }


    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @GetMapping("/my")
    public CoreApiResponse<List<ExportItemEntity>> getMyExportItems() {
        List<ExportItemEntity> exportItems = exportItemService.getMyExportItems();
        return CoreApiResponse.success(exportItems);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<ExportItemEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(exportItemService.getExportItemById(id));
    }

    @PostMapping("/create/multi")
    public CoreApiResponse<?> createExportItems(@RequestBody CreateExportItemsRequest request) {
        exportItemService.createExportItems(request);
        return CoreApiResponse.success(LocalizationUtils.getMessage("export.item.create.success"));
    }
}
