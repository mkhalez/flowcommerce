package com.coworking.space.orderservice.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderItemRequest {
    @Positive
    private int itemId;

    @Positive
    private int quantity;
}
