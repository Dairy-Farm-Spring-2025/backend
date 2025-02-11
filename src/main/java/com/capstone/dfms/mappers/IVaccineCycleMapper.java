package com.capstone.dfms.mappers;

import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.requests.VaccineCycleRequest;
import com.capstone.dfms.requests.VaccineCycleUpdateInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IVaccineCycleMapper {
    IVaccineCycleMapper INSTANCE = Mappers.getMapper(IVaccineCycleMapper.class);

    VaccineCycleEntity toModel(VaccineCycleRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVaccineCycleFromRequest(VaccineCycleUpdateInfo updateRequest, @MappingTarget VaccineCycleEntity warehouseEntity);
}
