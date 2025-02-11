package com.capstone.dfms.mappers;

import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.requests.ItemBatchRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IItemBatchMapper {
    IItemBatchMapper INSTANCE = Mappers.getMapper(IItemBatchMapper.class);

    @Mapping(source = "itemId", target = "itemEntity.itemId")
    @Mapping(source = "supplierId", target = "supplierEntity.supplierId")
    ItemBatchEntity toModel(ItemBatchRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "itemId", target = "itemEntity.itemId")
    @Mapping(source = "supplierId", target = "supplierEntity.supplierId")
    void updateItemBatchFromRequest(ItemBatchRequest updateRequest, @MappingTarget ItemBatchEntity itemBatch);
}
