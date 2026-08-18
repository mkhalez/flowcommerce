package com.coworking.space.userservice.mapper;

import com.coworking.space.userservice.domain.entities.UserEntity;
import com.coworking.space.userservice.dto.requests.UserCreateRequest;
import com.coworking.space.userservice.dto.requests.UserUpdateRequest;
import com.coworking.space.userservice.dto.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "authUserId", source = "authUserId")
    UserEntity toUserEntity(UserCreateRequest request, String authUserId);

    @Mapping(target = "cardsCount", source = "count")
    UserResponse toUserResponse(UserEntity entity, int count);

    void updateUserEntity(UserUpdateRequest request, @MappingTarget UserEntity entity);
}
