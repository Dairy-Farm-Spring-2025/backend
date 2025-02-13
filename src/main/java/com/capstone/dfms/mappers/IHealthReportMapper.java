package com.capstone.dfms.mappers;

import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.requests.HealthReportRequest;
import com.capstone.dfms.requests.IllnessDetailUpdateRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IHealthReportMapper {
    IHealthReportMapper INSTANCE = Mappers.getMapper(IHealthReportMapper.class);

    @Mapping(source = "cowId", target = "cowEntity.cowId")
    HealthRecordEntity toModel(HealthReportRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "cowId", target = "cowEntity.cowId")
    void updateEntityFromDto(HealthReportRequest dto, @MappingTarget HealthRecordEntity entity);
}
