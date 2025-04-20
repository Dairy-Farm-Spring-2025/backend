package com.capstone.dfms.mappers;

import com.capstone.dfms.models.SupplierEntity;
import com.capstone.dfms.requests.SupplierRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ISupplierMapper {
    ISupplierMapper INSTANCE = Mappers.getMapper(ISupplierMapper.class);
    SupplierEntity toModel(SupplierRequest request);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSupplierFromRequest(SupplierRequest updateRequest, @MappingTarget SupplierEntity supplierEntity);
}
