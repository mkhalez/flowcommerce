package com.coworking.space.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class OrderItemResponse {
    private int itemId;
    private String itemName;
    private double price;
    private int quantity;
    private int totalPrice;
}
