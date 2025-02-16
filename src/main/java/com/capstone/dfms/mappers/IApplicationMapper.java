package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.ApplicationCreateRequest;
import com.capstone.dfms.requests.ApplicationTypeRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IApplicationMapper {
    IApplicationTypeMapper INSTANCE = Mappers.getMapper(IApplicationTypeMapper.class);


    @Mapping(target = "approveBy", ignore = true)
    @Mapping(target = "requestBy.id", ignore = true)
    @Mapping(target = "type.applicationId", source = "typeId")
    @Mapping(target = "status", expression = "java(com.capstone.dfms.models.enums.ApplicationStatus.processing)")
    @Mapping(target = "requestDate", expression = "java(java.time.LocalDate.now())")
    ApplicationEntity toModel (ApplicationCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "", source = "approvalStatus", ignore = true)
    @Mapping(target = "approveDate", expression = "java(java.time.LocalDate.now())")
    void updateEntityFromDto(ApplicationApproveRequest dto, @MappingTarget ApplicationEntity entity);
}
