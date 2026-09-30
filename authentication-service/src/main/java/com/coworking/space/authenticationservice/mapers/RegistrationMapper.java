package com.coworking.space.authenticationservice.mapers;

import com.coworking.space.authenticationservice.domain.entities.RegistrationEventEntity;
import com.coworking.space.authenticationservice.dto.response.RegistrationStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    @Mapping(target = "transactionId", source = "id")
    RegistrationStatusResponse toRegistrationStatusResponse(RegistrationEventEntity entity);
}
