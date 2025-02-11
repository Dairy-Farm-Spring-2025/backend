package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.requests.ItemCreateRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "categoryId", target = "categoryEntity.categoryId")
    @Mapping(source = "locationId", target = "warehouseLocationEntity.warehouseLocationId")
    ItemEntity toModel(ItemCreateRequest request);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryEntity", ignore = true)
    @Mapping(target = "warehouseLocationEntity", ignore = true)
    void updateItemFromRequest(ItemCreateRequest updateRequest, @MappingTarget ItemEntity itemEntity);
}
