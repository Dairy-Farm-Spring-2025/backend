package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.requests.ReportTaskRequest;
import com.capstone.dfms.responses.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IReportTaskMapper {
    IReportTaskMapper INSTANCE = Mappers.getMapper(IReportTaskMapper.class);

    ReportTaskEntity toModel(ReportTaskRequest request);

    TaskResponse toResponse(TaskEntity entity);


}
