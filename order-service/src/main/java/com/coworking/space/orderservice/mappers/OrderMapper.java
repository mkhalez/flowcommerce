package com.coworking.space.orderservice.mappers;

import com.coworking.space.orderservice.domain.entities.OrderEntity;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", uses = OrderItemsMapper.class)
public interface OrderMapper extends BaseMapper {

    @Mapping(target = "items", source = "entity.orderItemsEntities")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", source = "entity.id")
    OrderResponse toOrderResponse(OrderEntity entity, UserResponse user);
}
