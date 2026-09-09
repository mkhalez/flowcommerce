package com.coworking.space.paymentservice.clients;

import com.coworking.space.paymentservice.dto.response.OrderResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface OrderServiceClient {
    @GetExchange("/{id}")
    OrderResponse findById(@PathVariable int id);
}
