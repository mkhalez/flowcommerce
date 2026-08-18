package com.coworking.space.userservice.clients;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.PatchExchange;

public interface AuthClient {
    @PatchExchange("/users/{id}/disable")
    void disableUserById(@PathVariable int id);

    @PatchExchange("/users/{id}/enable")
    void enableUserById(@PathVariable int id);

    @DeleteExchange("/users/{id}")
    void deleteById(@PathVariable int id);
}
