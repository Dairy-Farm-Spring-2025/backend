package com.capstone.dfms.mappers;

import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.repositories.IIllnessDetailRepository;
import com.capstone.dfms.requests.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IIllnessDetailMapper {
    IIllnessDetailMapper INSTANCE = Mappers.getMapper(IIllnessDetailMapper.class);

    @Mapping(target = "veterinarian.id", source = "veterinarianId")
    @Mapping(target = "vaccine.itemId", source = "itemId")
    @Mapping(target = "illnessEntity.illnessId", source = "illnessId")
    IllnessDetailEntity toModel(IllnessDetailCreateRequest request);

    @Mapping(target = "vaccine.itemId", source = "itemId")
    @Mapping(target = "illnessEntity.illnessId", source = "illnessId")
    IllnessDetailEntity toModel(IllnessDetailPlanRequest request);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(IllnessDetailReportRequest dto, @MappingTarget IllnessDetailEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "veterinarian.id", source = "veterinarianId")
    @Mapping(target = "vaccine.itemId", source = "itemId")
    @Mapping(target = "illnessEntity.illnessId", source = "illnessId")
    void updateEntityFromDto(IllnessDetailUpdateRequest dto, @MappingTarget IllnessDetailEntity entity);
}
