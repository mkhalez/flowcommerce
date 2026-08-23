package com.coworking.space.orderservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderRequest {
    private List<OrderItemRequest> items;

    private @Email String email;
}
