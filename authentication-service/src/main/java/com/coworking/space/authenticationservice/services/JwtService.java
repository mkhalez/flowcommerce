package com.coworking.space.authenticationservice.services;


import com.coworking.space.authenticationservice.domain.models.User;

public interface JwtService {
    String generateRefreshToken(User user);

    String generateAccessToken(User user);
}
