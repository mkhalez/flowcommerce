package com.coworking.space.orderservice.mappers;

import com.coworking.space.orderservice.domain.entities.ItemEntity;
import com.coworking.space.orderservice.dto.request.CreateItemRequest;
import com.coworking.space.orderservice.dto.request.UpdateItemRequest;
import com.coworking.space.orderservice.dto.response.ItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ItemMapper extends BaseMapper{
    ItemEntity toEntity(CreateItemRequest request);

    ItemResponse toResponse(ItemEntity entity);

    void updateEntity(UpdateItemRequest request, @MappingTarget ItemEntity entity);
}
