package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.repositories.IApplicationTypeRepository;
import com.capstone.dfms.requests.ApplicationTypeRequest;
import com.capstone.dfms.requests.HealthReportRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IApplicationTypeMapper {
    IApplicationTypeRepository INSTANCE = Mappers.getMapper(IApplicationTypeRepository.class);

    ApplicationTypeEntity toModel(ApplicationTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ApplicationTypeRequest dto, @MappingTarget ApplicationTypeEntity entity);
}
