package com.coworking.space.authenticationservice.services;

import com.coworking.space.authenticationservice.dto.request.UserRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;

public interface UserService {
    AuthResponse register(UserRequest user);
}
