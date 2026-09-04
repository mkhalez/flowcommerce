package com.coworking.space.authenticationservice.services;

import com.coworking.space.authenticationservice.dto.request.LoginRequest;
import com.coworking.space.authenticationservice.dto.request.RefreshRequest;
import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;
import com.coworking.space.authenticationservice.dto.response.RegistrationStatusResponse;

import java.util.UUID;

public interface AuthService {
    RegistrationStatusResponse register(SingUpRequest user, byte[] payload);

    AuthResponse authenticate(LoginRequest user);

    AuthResponse accessToken(RefreshRequest refreshRequest);

    RegistrationStatusResponse checkRegistrationStatus(UUID registrationId);
}
