package com.capstone.dfms.mappers;

import com.capstone.dfms.models.UseEquipmentEntity;
import com.capstone.dfms.models.VaccineCycleDetailEntity;
import com.capstone.dfms.requests.UpdateVaccineCycleDetailRequest;
import com.capstone.dfms.requests.UseEquipmentEntityRequest;
import com.capstone.dfms.requests.UseEquipmentUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IUseEquipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    @Mapping(target = "taskType", ignore = true)
    UseEquipmentEntity toModel(UseEquipmentEntityRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UseEquipmentUpdateRequest dto, @MappingTarget UseEquipmentEntity entity);
}
