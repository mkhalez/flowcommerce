package com.coworking.space.authenticationservice.mapers;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.authenticationservice.dto.request.UserCreateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    UserRegistrationRequest toUserRegistrationRequest(UserCreateRequest request);
}
