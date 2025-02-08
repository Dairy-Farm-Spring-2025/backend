package com.capstone.dfms.mappers;

import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.requests.IllnessCreateRequest;
import com.capstone.dfms.requests.IllnessPrognosisRequest;
import com.capstone.dfms.requests.IllnessReportRequest;
import com.capstone.dfms.requests.IllnessUpdateRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IIllnessMapper {
    IIllnessMapper INSTANCE = Mappers.getMapper(IIllnessMapper.class);

    @Mapping(source = "cowId", target = "cowEntity.cowId")
    IllnessEntity toModel(IllnessReportRequest illnessReportRequest);

    @Mapping(source = "cowId", target = "cowEntity.cowId")
    IllnessEntity toModel(IllnessCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIllnessEntityFromDto(IllnessPrognosisRequest dto, @MappingTarget IllnessEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIllnessEntityFromDto(IllnessUpdateRequest dto, @MappingTarget IllnessEntity entity);
}
