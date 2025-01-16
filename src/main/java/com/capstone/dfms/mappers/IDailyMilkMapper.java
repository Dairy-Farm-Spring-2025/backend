package com.capstone.dfms.mappers;

import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.requests.DailyMilkRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IDailyMilkMapper {
    IDailyMilkMapper INSTANCE = Mappers.getMapper(IDailyMilkMapper.class);

    @Mapping(source = "cowId", target = "cow.cowId")
    DailyMilkEntity toModel (DailyMilkRequest request);

}
