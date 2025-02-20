package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IWarehouseMapper;
import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.repositories.IWarehouseLocationRepository;
import com.capstone.dfms.requests.WarehouseUpdateRequest;
import com.capstone.dfms.services.IWarehouseLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseLocationService implements IWarehouseLocationService {
    private final IWarehouseLocationRepository warehouseLocationRepository;

    private final IWarehouseMapper warehouseMapper;

    @Override
    public WarehouseLocationEntity createWareHouse(WarehouseLocationEntity warehouseLocation) {
        warehouseLocation.setName(StringUtils.NameStandardlizing(warehouseLocation.getName()));
        
        return warehouseLocationRepository.save(warehouseLocation);
    }

    @Override
    public WarehouseLocationEntity getWarehouseById(long id) {
        return warehouseLocationRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This warehouse is not existed!"));
    }

    @Override
    public List<WarehouseLocationEntity> getAllWareHouses() {
        return warehouseLocationRepository.findAll();
    }

    @Override
    public WarehouseLocationEntity updateWarehouse(Long id, WarehouseUpdateRequest request) {
        WarehouseLocationEntity warehouseLocation = warehouseLocationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Warehouse", "id", id));
        warehouseMapper.updateWarehouseFromRequest(request, warehouseLocation);
        return warehouseLocationRepository.save(warehouseLocation);
    }

    @Override
    public void deleteWareHouse(long id) {
        WarehouseLocationEntity warehouseLocation = warehouseLocationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Warehouse", "id", id));

        warehouseLocationRepository.delete(warehouseLocation);
    }
}
