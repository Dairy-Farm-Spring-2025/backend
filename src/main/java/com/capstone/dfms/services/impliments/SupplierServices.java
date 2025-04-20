package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.ISupplierMapper;
import com.capstone.dfms.models.SupplierEntity;
import com.capstone.dfms.repositories.ISupplierRepository;
import com.capstone.dfms.requests.SupplierRequest;
import com.capstone.dfms.services.ISupplierServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServices implements ISupplierServices {
    private final ISupplierRepository supplierRepository;

    private final ISupplierMapper supplierMapper;

    @Override
    public SupplierEntity createSupplier(SupplierEntity warehouseLocation) {
        warehouseLocation.setName(StringUtils.NameStandardlizing(warehouseLocation.getName()));

        return supplierRepository.save(warehouseLocation);
    }

    @Override
    public SupplierEntity getSupplierById(long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("supplier.not_found")
                ));
    }

    @Override
    public List<SupplierEntity> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public SupplierEntity updateSupplier(Long id, SupplierRequest request) {
        SupplierEntity supplierEntity = supplierRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Warehouse", "id", id));

        supplierMapper.updateSupplierFromRequest(request, supplierEntity);



        return supplierRepository.save(supplierEntity);
    }

    @Override
    public void deleteSupplier(long id) {
        SupplierEntity warehouseLocation = supplierRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Warehouse", "id", id));

        supplierRepository.delete(warehouseLocation);
    }
}
