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

    @Mapping(source = "dateOfBirth", target = "dateOfBirth") // Make sure it's correctly mapped
    @Mapping(source = "dateOfEnter", target = "dateOfEnter") // Make sure it's correctly mapped
    @Mapping(source = "cowTypeId", target = "cowTypeEntity.cowTypeId")
    CowEntity toModel(CowCreateRequest request);

    @Mapping(source = "dateOfBirth", target = "dateOfBirth") // Make sure it's correctly mapped
    @Mapping(source = "dateOfEnter", target = "dateOfEnter") // Make sure it's correctly mapped
    @Mapping(source = "cowTypeId", target = "cowTypeEntity.cowTypeId")
    CowEntity toModel(CowUpdateRequest request);

    @Mapping(source = "cowStatus", target = "cowStatus")  // Ensure correct mapping
    @Mapping(source = "cowOrigin", target = "cowOrigin")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "cowTypeName", target = "cowTypeEntity.name")
    CowEntity toModel(CowExcelCreateRequest row); // ✅ Add this line

    @Mapping(source = "cowTypeName", target = "cowTypeEntity.name")
    CowEntity toModel(ExcelCowRequest row); // ✅ Add this line
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCowFromRequest(CowUpdateRequest updateRequest, @MappingTarget CowEntity cowEntity);
    @Mapping(source = "cowTypeEntity", target = "cowType")
    CowResponse toResponse(CowEntity entity);
}
