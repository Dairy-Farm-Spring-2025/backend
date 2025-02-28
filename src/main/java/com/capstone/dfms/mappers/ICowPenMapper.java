package com.capstone.dfms.mappers;

import com.capstone.dfms.models.CowPenEntity;
import com.capstone.dfms.models.compositeKeys.CowPenPK;
import com.capstone.dfms.requests.CowPenCreateRequest;
import com.capstone.dfms.requests.CowPenUpdateRequest;
import com.capstone.dfms.responses.CowPenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
@Mapper(componentModel = "spring")
public interface ICowPenMapper {
    ICowPenMapper INSTANCE = Mappers.getMapper(ICowPenMapper.class);

    @Mapping(source = "penId", target = "id.penId")
    @Mapping(source = "cowId", target = "id.cowId")
    //@Mapping(source = "fromDate", target = "id.fromDate")
    CowPenEntity toModel(CowPenCreateRequest request);

    CowPenEntity toModel(CowPenUpdateRequest request);

    @Mapping(target = "fromDate", source = "id.fromDate")
    CowPenResponse toResponse(CowPenEntity entity);

    default CowPenPK toCowPenPK(Long penId, Long cowId, LocalDate fromDate) {
        return new CowPenPK(penId, cowId, fromDate);
    }
}
