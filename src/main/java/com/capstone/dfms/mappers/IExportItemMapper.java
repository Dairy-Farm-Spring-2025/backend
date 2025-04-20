package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ExportItemEntity;
import com.capstone.dfms.requests.ExportItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IExportItemMapper {
    IExportItemMapper INSTANCE = Mappers.getMapper(IExportItemMapper.class);

    ExportItemEntity toModel(ExportItemRequest request);

}
