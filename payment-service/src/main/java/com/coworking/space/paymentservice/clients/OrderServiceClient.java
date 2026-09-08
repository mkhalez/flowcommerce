package com.coworking.space.paymentservice.clients;

import com.coworking.space.paymentservice.dto.response.OrderResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface OrderServiceClient {
    @GetMapping("/{id}")
    OrderResponse findById(@PathVariable int id);
}
