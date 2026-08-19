package com.coworking.space.orderservice.dto.response;

import com.coworking.space.orderservice.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ItemResponse {
    private int id;
    private int userId;
    private Status status;
    private double totalPrice;
    private OffsetDateTime createAt;
    private List<OrderItemResponse> items;
}
