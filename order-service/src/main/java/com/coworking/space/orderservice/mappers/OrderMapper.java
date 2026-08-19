package com.coworking.space.orderservice.mappers;

import com.coworking.space.orderservice.domain.entities.OrderEntity;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toOrderResponse(OrderEntity entity);
}
