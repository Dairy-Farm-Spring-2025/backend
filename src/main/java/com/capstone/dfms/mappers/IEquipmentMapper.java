package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.models.EquipmentEntity;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.EquipmentRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IEquipmentMapper {
    IEquipmentMapper INSTANCE = Mappers.getMapper(IEquipmentMapper.class);

    @Mapping(source = "locationId", target = "warehouseLocationEntity.warehouseLocationId")
    EquipmentEntity toModel(EquipmentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "warehouseLocationEntity", ignore = true)
    void updateEntityFromDto(EquipmentRequest dto, @MappingTarget EquipmentEntity entity);
}
