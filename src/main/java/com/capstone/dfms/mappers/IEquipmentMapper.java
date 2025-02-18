package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.models.EquipmentEntity;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.EquipmentRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IEquipmentMapper {
    IEquipmentMapper INSTANCE = Mappers.getMapper(IEquipmentMapper.class);

    EquipmentEntity toModel(EquipmentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EquipmentRequest dto, @MappingTarget EquipmentEntity entity);
}
