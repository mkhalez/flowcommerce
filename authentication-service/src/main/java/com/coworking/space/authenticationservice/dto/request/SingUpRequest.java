package com.coworking.space.authenticationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SingUpRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
