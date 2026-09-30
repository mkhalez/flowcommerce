package com.coworking.space.userservice.mapper;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.userservice.dto.requests.UserCreateRequest;
import org.mapstruct.Mapper;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    UserCreateRequest toUserCreateRequest(UserRegistrationRequest request);

    default LocalDate toLocalDate(int epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }

    default String map(CharSequence value) {
        return value != null ? value.toString() : null;
    }
}
