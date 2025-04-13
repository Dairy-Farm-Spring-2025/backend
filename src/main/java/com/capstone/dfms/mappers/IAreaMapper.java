package com.capstone.dfms.mappers;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.requests.AreaCreateRequest;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IAreaMapper {
    IAreaMapper INSTANCE = Mappers.getMapper(IAreaMapper.class);

    @Mapping(source = "cowTypeId", target = "cowTypeEntity.cowTypeId")
    AreaEntity toModel(AreaCreateRequest request);

    AreaEntity toModel(AreaUpdateRequest request);

    AreaResponse toResponse(AreaEntity entity);

    @Mapping(target = "cowTypeEntity", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAreaFromRequest(AreaUpdateRequest updateRequest, @MappingTarget AreaEntity areaEntity);

    @Named("mapCowTypeId")
    default CowTypeEntity mapCowTypeId(Long cowTypeId) {
        if (cowTypeId == null) {
            return null;
        }
        CowTypeEntity cowTypeEntity = new CowTypeEntity();
        cowTypeEntity.setCowTypeId(cowTypeId);
        return cowTypeEntity;
    }
}
