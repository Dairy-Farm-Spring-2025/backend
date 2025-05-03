package com.capstone.dfms.mappers;

import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.requests.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IIllnessMapper {
    IIllnessMapper INSTANCE = Mappers.getMapper(IIllnessMapper.class);

    @Mapping(source = "cowId", target = "cowEntity.cowId")
    IllnessEntity toModel(IllnessReportRequest illnessReportRequest);

    @Mapping(source = "cowId", target = "cowEntity.cowId")
    @Mapping(source = "detail", target = "illnessDetails")
    IllnessEntity toModel(IllnessCreateRequest request);


    @Mapping(source = "cowId", target = "cowEntity.cowId")
    IllnessEntity toModel(IllnessCreateOgrRequest request);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIllnessEntityFromDto(IllnessUpdateRequest dto, @MappingTarget IllnessEntity entity);
}
