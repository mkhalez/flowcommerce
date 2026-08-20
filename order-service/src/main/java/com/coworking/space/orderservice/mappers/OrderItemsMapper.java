package com.coworking.space.orderservice.mappers;

import com.coworking.space.orderservice.domain.entities.OrderItemsEntity;
import com.coworking.space.orderservice.dto.response.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemsMapper {
    @Mapping(target = "itemId", source = "item.getId()")
    @Mapping(target = "itemName", source = "item.getName()")
    @Mapping(target = "price", source = "item.getPrice()")
    @Mapping(
            target = "totalPrice",
            expression = "java(entity.getItem().getPrice() * entity.getQuantity())")
    OrderItemResponse toOrderItemResponse(OrderItemsEntity entity);
}
