package com.coworking.space.authenticationservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;

    private String refreshToken;
}
