package com.coworking.space.paymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class OrderResponse {
    private int id;
    private OrderStatusResponse status;
    private double totalPrice;
    private UserResponse user;
}
