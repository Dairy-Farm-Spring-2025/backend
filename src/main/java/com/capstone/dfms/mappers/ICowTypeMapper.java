package com.capstone.dfms.mappers;

import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.requests.CowTypeCreateRequest;
import com.capstone.dfms.requests.CowTypeUpdateRequest;
import com.capstone.dfms.responses.CowTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ICowTypeMapper {
    ICowTypeMapper INSTANCE = Mappers.getMapper(ICowTypeMapper.class);
    CowTypeEntity toModel(CowTypeCreateRequest request);
    CowTypeEntity toModel(CowTypeUpdateRequest request);
    CowTypeResponse toResponse(CowTypeEntity entity);
}
