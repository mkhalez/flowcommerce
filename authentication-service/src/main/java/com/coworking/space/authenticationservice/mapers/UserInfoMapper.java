package com.coworking.space.authenticationservice.mapers;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.authenticationservice.dto.payload.UserRegistrationPayload;
import com.coworking.space.authenticationservice.dto.request.UserCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    @Mapping(target = "authId", source = "id")
    UserRegistrationPayload toPayload(UserCreateRequest request, int id);

    UserRegistrationRequest toUserRegistrationRequest(UserRegistrationPayload userRegistrationPayload);

    default int mapDate(LocalDate date) {
        return (int)date.toEpochDay();
    }
}
