package com.capstone.dfms.mappers;

import com.capstone.dfms.models.VaccineInjectionEntity;
import com.capstone.dfms.requests.VaccineInjectionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface IVaccineInjectionMapper {

    @Mapping(source = "cowId", target = "cowEntity.cowId")
    @Mapping(source = "vaccineCycleDetailId", target = "vaccineCycleDetail.vaccineCycleDetailId")
    VaccineInjectionEntity toModel(VaccineInjectionRequest request);
}
