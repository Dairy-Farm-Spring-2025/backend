package com.capstone.dfms.mappers;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.requests.AreaCreateRequest;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IAreaMapper {
    IAreaMapper INSTANCE = Mappers.getMapper(IAreaMapper.class);
    AreaEntity toModel(AreaCreateRequest request);
    AreaEntity toModel(AreaUpdateRequest request);
    AreaResponse toResponse(AreaEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAreaFromRequest(AreaUpdateRequest updateRequest, @MappingTarget AreaEntity areaEntity);
}
