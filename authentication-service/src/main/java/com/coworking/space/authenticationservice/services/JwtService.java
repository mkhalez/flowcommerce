package com.coworking.space.authenticationservice.services;


import com.coworking.space.authenticationservice.domain.models.Role;
import com.coworking.space.authenticationservice.domain.models.User;

import java.time.Instant;
import java.util.Set;

public interface JwtService {
    String generateAccessToken(String  username, Set<Role> roles);

    String generateRefreshToken(String  username);

}
