package com.capstone.dfms.mappers;

import com.capstone.dfms.models.VaccineCycleDetailEntity;
import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.requests.UpdateVaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailCreateRequest;
import com.capstone.dfms.requests.VaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailUpdateRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IVaccineCycleDetailMapper {
    IVaccineCycleDetailMapper INSTANCE = Mappers.getMapper(IVaccineCycleDetailMapper.class);

    @Mapping(source = "itemId", target = "itemEntity.itemId")
    VaccineCycleDetailEntity toModel(VaccineCycleDetailRequest request);

    @Mapping(source = "itemId", target = "itemEntity.itemId")
    VaccineCycleDetailEntity toModel(VaccineCycleDetailCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "itemId", target = "itemEntity.itemId")
    void updateEntityFromDto(UpdateVaccineCycleDetailRequest dto, @MappingTarget VaccineCycleDetailEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "itemEntity", ignore = true)
    void updateEntityFromDto(VaccineCycleDetailUpdateRequest dto, @MappingTarget VaccineCycleDetailEntity entity);
}
