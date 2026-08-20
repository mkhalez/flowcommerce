package com.coworking.space.orderservice.dto.response;

import com.coworking.space.orderservice.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
public class OrderResponse {
    private int id;
    private Status status;
    private double totalPrice;
    private OffsetDateTime createdAt;
    private Set<OrderItemResponse> items;
    private UserResponse user;
}
