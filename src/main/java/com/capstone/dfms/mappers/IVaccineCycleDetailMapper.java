package com.capstone.dfms.mappers;

import com.capstone.dfms.models.VaccineCycleDetailEntity;
import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.requests.UpdateVaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IVaccineCycleDetailMapper {
    IVaccineCycleDetailMapper INSTANCE = Mappers.getMapper(IVaccineCycleDetailMapper.class);

    @Mapping(source = "itemId", target = "itemEntity.itemId")
    VaccineCycleDetailEntity toModel(VaccineCycleDetailRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "itemId", target = "itemEntity.itemId")
//    @Mapping(target = "vaccineCycleDetailId", ignore = true)
    void updateEntityFromDto(UpdateVaccineCycleDetailRequest dto, @MappingTarget VaccineCycleDetailEntity entity);
}
