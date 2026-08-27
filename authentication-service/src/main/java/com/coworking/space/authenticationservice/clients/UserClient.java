package com.coworking.space.authenticationservice.clients;

import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.request.UserCreateRequest;
import com.coworking.space.authenticationservice.dto.request.UserServiceRequest;
import com.coworking.space.authenticationservice.dto.response.UserResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface UserClient {
    @PostExchange
    UserResponse createUser(@RequestBody UserServiceRequest request);
}
