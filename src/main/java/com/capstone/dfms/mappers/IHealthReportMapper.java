package com.capstone.dfms.mappers;

import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.requests.HealthRecordCreateRequest;
import com.capstone.dfms.requests.HealthRecordExcelRequest;
import com.capstone.dfms.requests.HealthReportRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IHealthReportMapper {
    IHealthReportMapper INSTANCE = Mappers.getMapper(IHealthReportMapper.class);

    @Mapping(source = "cowId", target = "cowEntity.cowId")
    HealthRecordEntity toModel(HealthReportRequest request);

    HealthRecordEntity toModel(HealthRecordCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "cowId", target = "cowEntity.cowId")
    void updateEntityFromDto(HealthReportRequest dto, @MappingTarget HealthRecordEntity entity);

    @Mapping(target = "healthRecordId", ignore = true)
    @Mapping(target = "cowEntity.name", source = "cowName")
    @Mapping(target = "status", expression = "java(request.getHealthRecordStatus())")
    @Mapping(target = "reportTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "weight", ignore = true)
    HealthRecordEntity toModel(HealthRecordExcelRequest request);
}
