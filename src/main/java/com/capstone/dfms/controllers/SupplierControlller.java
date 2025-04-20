package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.SupplierEntity;
import com.capstone.dfms.requests.SupplierRequest;
import com.capstone.dfms.services.ISupplierServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.ISupplierMapper.INSTANCE;
@RestController
@RequestMapping("${app.api.version.v1}/suppliers")
@RequiredArgsConstructor
public class SupplierControlller {
    private final ISupplierServices supplierServices;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createSupplier(
            @Valid @RequestBody SupplierRequest request
    ){
         supplierServices.createSupplier(INSTANCE.toModel(request));
        return CoreApiResponse.success("Create supplier successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<SupplierEntity>> getAll() {
        return CoreApiResponse.success(supplierServices.getAllSuppliers());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<SupplierEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(supplierServices.getSupplierById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteSupplier(
            @PathVariable Long id
    ){
        supplierServices.deleteSupplier(id);
        return CoreApiResponse.success("Delete supplier successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<?> updateWarehouse(
            @PathVariable Long id,
            @RequestBody SupplierRequest request) {
        supplierServices.updateSupplier(id,request );
        return CoreApiResponse.success("supplier updated successfully.");
    }

}
