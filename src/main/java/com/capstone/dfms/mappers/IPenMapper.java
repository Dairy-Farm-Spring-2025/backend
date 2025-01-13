package com.capstone.dfms.mappers;

import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.requests.PenCreateRequest;
import com.capstone.dfms.requests.PenUpdateRequest;
import com.capstone.dfms.responses.PenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IPenMapper {
    IPenMapper INSTANCE = Mappers.getMapper(IPenMapper.class);

    @Mapping(source = "areaId", target = "areaBelongto.areaId")
    PenEntity toModel(PenCreateRequest request);
    @Mapping(source = "areaId", target = "areaBelongto.areaId")
    PenEntity toModel(PenUpdateRequest request);
    @Mapping(target = "area", source = "areaBelongto")
    PenResponse toResponse(PenEntity entity);
}
