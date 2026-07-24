package com.coworking.space.userservice.mapper;

import com.coworking.space.userservice.domain.entities.CardEntity;
import com.coworking.space.userservice.domain.entities.UserEntity;
import com.coworking.space.userservice.dto.requests.CardCreateRequest;
import com.coworking.space.userservice.dto.requests.CardUpdateRequest;
import com.coworking.space.userservice.dto.responses.CardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {

    @Mapping(target = "userId", source = "userId")
    CardResponse toCardResponse(CardEntity entity, int userId);

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "user", source = "user")
    CardEntity toCardEntity(CardCreateRequest request, UserEntity user);

    void updateCardEntity(CardUpdateRequest request, @MappingTarget CardEntity entity);
}
