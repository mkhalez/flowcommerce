package com.coworking.space.authenticationservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingUpRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Valid
    @NotNull
    private UserCreateRequest userInfo;
}
