package com.capstone.dfms.services;

import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.requests.WarehouseUpdateRequest;

import java.util.List;

public interface IWarehouseLocationService {
    WarehouseLocationEntity createWareHouse(WarehouseLocationEntity warehouseLocation);

    WarehouseLocationEntity getWarehouseById(long id);

    List<WarehouseLocationEntity> getAllWareHouses();

    WarehouseLocationEntity updateWarehouse(Long id, WarehouseUpdateRequest request);

    void deleteWareHouse(long id);
}
