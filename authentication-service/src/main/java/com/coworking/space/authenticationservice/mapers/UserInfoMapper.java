package com.coworking.space.authenticationservice.mapers;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.authenticationservice.dto.request.UserCreateRequest;
import org.mapstruct.Mapper;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    UserRegistrationRequest toUserRegistrationRequest(UserCreateRequest request);

    default int mapDate(LocalDate date) {
        return (int)date.toEpochDay();
    }
}
