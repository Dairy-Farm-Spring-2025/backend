package com.capstone.dfms.mappers;

import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.requests.CreateAccountRequest;
import com.capstone.dfms.requests.PersonalUpdateRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(source = "roleId", target = "roleId.id")
    UserEntity toModel(CreateAccountRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(PersonalUpdateRequest updateRequest, @MappingTarget UserEntity user);


}
