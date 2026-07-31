package com.coworking.space.authenticationservice.dto.request;

import lombok.Data;

@Data
public class SingUpRequest {
    private String username;

    private String password;
}
