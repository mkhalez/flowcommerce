package com.coworking.space.orderservice.clients;

import com.coworking.space.orderservice.dto.response.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface UserServiceClient {

    @GetExchange("/user")
    @CircuitBreaker(name = "UserServiceCB", fallbackMethod = "findUserByEmailFallBack")
    UserResponse findUserByEmail(@RequestParam String email);

    @GetExchange("/{id}")
    @CircuitBreaker(name = "UserServiceCB", fallbackMethod = "findUserByIdFallBack")
    UserResponse findById(@PathVariable int id);

    default UserResponse findUserByEmailFallBack(String email, Throwable throwable) {
        return UserResponse.builder()
                .name("n/a")
                .surname("n/a")
                .email(email)
                .build();
    }

    default UserResponse findUserByIdFallBack(int id, Throwable throwable) {
        return UserResponse.builder()
                .id(id)
                .name("n/a")
                .surname("n/a")
                .build();
    }
}
