package com.coworking.space.authenticationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingUpRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
