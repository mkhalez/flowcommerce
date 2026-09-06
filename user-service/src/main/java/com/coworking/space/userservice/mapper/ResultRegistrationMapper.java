package com.coworking.space.userservice.mapper;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationResult;
import com.coworking.space.userservice.dto.payload.UserRegistrationPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResultRegistrationMapper {
    UserRegistrationResult toUserRegistrationResultAvro(UserRegistrationPayload payload);
}
