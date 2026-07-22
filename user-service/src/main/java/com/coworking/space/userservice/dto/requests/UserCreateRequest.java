package com.coworking.space.userservice.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserCreateRequest(
    @NotBlank String name,
    @NotBlank String surname,
    @NotNull @Past LocalDate birthDate,
    @Email String email) {}
