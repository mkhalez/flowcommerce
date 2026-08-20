package com.coworking.space.orderservice.dto.response;

import lombok.*;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class OrderItemResponse {
    private int itemId;
    private String itemName;
    private double price;
    private int quantity;
    private double totalPrice;
}
