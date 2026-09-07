package com.coworking.space.authenticationservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public record UserCreateRequest(
        @NotBlank String name,
        @NotBlank String surname,
        @NotNull @Past LocalDate birthDay,
        @Email String email,
        Integer authId) {}
