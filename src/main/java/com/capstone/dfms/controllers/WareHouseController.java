package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.requests.WarehouseUpdateRequest;
import com.capstone.dfms.services.IWarehouseLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IWarehouseMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/warehouses")
@RequiredArgsConstructor
public class WareHouseController {
    private final IWarehouseLocationService warehouseLocationService;


    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createWarehouse(
            @Valid @RequestBody WarehouseUpdateRequest request
    ){
        var areaResponse = warehouseLocationService.createWareHouse(INSTANCE.toModel(request));
        return CoreApiResponse.success("Create warehouse successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<WarehouseLocationEntity>> getAll() {
        return CoreApiResponse.success(warehouseLocationService.getAllWareHouses());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<WarehouseLocationEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(warehouseLocationService.getWarehouseById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteWarehouse(
            @PathVariable Long id
    ){
        warehouseLocationService.deleteWareHouse(id);
        return CoreApiResponse.success("Delete warehouse successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<?> updateWarehouse(
            @PathVariable Long id,
            @RequestBody WarehouseUpdateRequest request) {
        warehouseLocationService.updateWarehouse(id,request );
        return CoreApiResponse.success("Warehouse updated successfully.");
    }

}
