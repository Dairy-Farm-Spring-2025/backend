package com.capstone.dfms.mappers;

import com.capstone.dfms.models.TaskTypeEntity;
import com.capstone.dfms.requests.TaskTypeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ITaskTypeMapper {
    ITaskTypeMapper INSTANCE = Mappers.getMapper(ITaskTypeMapper.class);
    @Mapping(source = "roleId", target = "roleId.id")
    TaskTypeEntity toModel(TaskTypeRequest request);
}
