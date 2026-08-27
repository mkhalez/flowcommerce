package com.coworking.space.authenticationservice.mapers;

import com.coworking.space.authenticationservice.dto.request.UserCreateRequest;
import com.coworking.space.authenticationservice.dto.request.UserServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {
    @Mapping(source = "authId", target = "authId")
    UserServiceRequest toUserServiceRequest(UserCreateRequest request, int authId);
}
