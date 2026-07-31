package com.coworking.space.authenticationservice.services;

import com.coworking.space.authenticationservice.dto.request.LoginRequest;
import com.coworking.space.authenticationservice.dto.request.RefreshRequest;
import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(SingUpRequest user);

    AuthResponse authenticate(LoginRequest user);

    AuthResponse accessToken(RefreshRequest refreshRequest);
}
