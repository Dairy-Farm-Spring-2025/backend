package com.capstone.dfms.mappers;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.requests.AreaCreateRequest;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IAreaMapper {
    IAreaMapper INSTANCE = Mappers.getMapper(IAreaMapper.class);
    AreaEntity toModel(AreaCreateRequest request);
    AreaEntity toModel(AreaUpdateRequest request);
    AreaResponse toResponse(AreaEntity entity);
}
