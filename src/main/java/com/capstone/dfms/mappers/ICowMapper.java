package com.capstone.dfms.mappers;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.requests.CowCreateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.responses.CowResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ICowMapper {
    ICowMapper INSTANCE = Mappers.getMapper(ICowMapper.class);

    @Mapping(source = "dateOfBirth", target = "dateOfBirth") // Make sure it's correctly mapped
    @Mapping(source = "dateOfEnter", target = "dateOfEnter") // Make sure it's correctly mapped
    @Mapping(source = "cowTypeId", target = "cowTypeEntity.cowTypeId")
    CowEntity toModel(CowCreateRequest request);

    @Mapping(source = "dateOfBirth", target = "dateOfBirth") // Make sure it's correctly mapped
    @Mapping(source = "dateOfEnter", target = "dateOfEnter") // Make sure it's correctly mapped
    @Mapping(source = "cowTypeId", target = "cowTypeEntity.cowTypeId")
    CowEntity toModel(CowUpdateRequest request);
    @Mapping(source = "cowTypeEntity", target = "cowType")
    CowResponse toResponse(CowEntity entity);
}
