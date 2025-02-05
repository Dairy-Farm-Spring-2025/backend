package com.capstone.dfms.services;

import com.capstone.dfms.models.SupplierEntity;
import com.capstone.dfms.requests.SupplierRequest;

import java.util.List;

public interface ISupplierServices {
    SupplierEntity createSupplier(SupplierEntity warehouseLocation);

    SupplierEntity getSupplierById(long id);

    List<SupplierEntity> getAllSuppliers();

    SupplierEntity updateSupplier(Long id, SupplierRequest request);

    void deleteSupplier(long id);
}
