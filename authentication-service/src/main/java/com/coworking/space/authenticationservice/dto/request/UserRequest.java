package com.coworking.space.authenticationservice.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String username;

    private String password;
}
