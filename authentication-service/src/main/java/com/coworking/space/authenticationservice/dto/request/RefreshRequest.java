package com.coworking.space.authenticationservice.dto.request;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
