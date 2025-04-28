package com.capstone.dfms.mappers;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.CowResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ICowMapper {
    ICowMapper INSTANCE = Mappers.getMapper(ICowMapper.class);

    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "dateOfEnter", target = "dateOfEnter")
    @Mapping(source = "cowTypeId", target = "cowTypeEntity.cowTypeId")
    CowEntity toModel(CowCreateRequest request);

    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "dateOfEnter", target = "dateOfEnter")
    @Mapping(source = "cowTypeId", target = "cowTypeEntity.cowTypeId")
    CowEntity toModel(CowUpdateRequest request);

    @Mapping(source = "cowStatus", target = "cowStatus")
    @Mapping(source = "cowOrigin", target = "cowOrigin")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "cowTypeName", target = "cowTypeEntity.name")
    CowEntity toModel(CowExcelCreateRequest row);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cowStatus", ignore = true)
    void updateCowFromRequest(CowUpdateRequest updateRequest, @MappingTarget CowEntity cowEntity);

    @Mapping(source = "cowTypeEntity", target = "cowType")
    CowResponse toResponse(CowEntity entity);
}
