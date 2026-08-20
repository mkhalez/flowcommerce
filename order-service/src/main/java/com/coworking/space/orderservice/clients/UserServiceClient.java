package com.coworking.space.orderservice.clients;

import com.coworking.space.orderservice.dto.response.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface UserServiceClient {
    @GetExchange("/user")
    UserResponse findUserByEmail(@RequestParam String email);

    @GetExchange("/{id}")
    UserResponse findById(@PathVariable int id);
}
