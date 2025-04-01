package com.capstone.dfms.mappers;

import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.responses.MaterialResponse;
import com.capstone.dfms.responses.RangeTaskResponse;
import com.capstone.dfms.responses.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITaskMapper {
    ITaskMapper INSTANCE = Mappers.getMapper(ITaskMapper.class);
//    @Mapping(target = "areaName", source = "areaId.name")
//    @Mapping(target = "taskTypeName", source = "taskTypeId.name")
    @Mapping(target = "assignerName", source = "assigner.name")
    @Mapping(target = "assigneeName", source = "assignee.name")
    @Mapping(target = "reportTask", ignore = true) // Ignore vì sẽ set thủ công
    TaskResponse toResponse(TaskEntity entity);

//    @Mapping(target = "areaName", source = "areaId.name")
//    @Mapping(target = "taskTypeName", source = "taskTypeId.name")
    @Mapping(target = "assignerName", source = "assigner.name")
    @Mapping(target = "assigneeName", source = "assignee.name")
    List<TaskResponse> toResponseList(List<TaskEntity> entities);

    @Mapping(target = "assignerName", source = "assigner.name")
    @Mapping(target = "assigneeName", source = "assignee.name")
    @Mapping(target = "material", expression = "java(mapToMaterialResponse(entity))")
    RangeTaskResponse toResponse2(TaskEntity entity);


    @Mapping(target = "assignerName", source = "assigner.name")
    @Mapping(target = "assigneeName", source = "assignee.name")
    @Mapping(target = "material", expression = "java(mapToMaterialResponse(entity))")
    List<RangeTaskResponse> toResponseList2(List<TaskEntity> entities);


    @Named("mapToMaterialResponse")
    default MaterialResponse mapToMaterialResponse(TaskEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MaterialResponse(
                entity.getIllness(),
                entity.getVaccineInjection(),
                entity.getMainIllness()
        );
    }

}
