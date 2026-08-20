package com.coworking.space.orderservice.mappers;

import com.coworking.space.orderservice.domain.entities.OrderEntity;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemsMapper.class)
public interface OrderMapper {

    @Mapping(target = "items", source = "entity.orderItemsEntities")
    @Mapping(target = "user", source = "user")
    OrderResponse toOrderResponse(OrderEntity entity, UserResponse user);
}
