package com.capstone.dfms.mappers;

import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.requests.WarehouseUpdateRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IWarehouseMapper {

    WarehouseLocationEntity toModel(WarehouseUpdateRequest request);

    IWarehouseMapper INSTANCE = Mappers.getMapper(IWarehouseMapper.class);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWarehouseFromRequest(WarehouseUpdateRequest updateRequest, @MappingTarget WarehouseLocationEntity warehouseEntity);
}
