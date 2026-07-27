package com.coworking.space.userservice.dto.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CardCreateRequest(
        @NotNull int userId,
        @NotBlank @Pattern(regexp = "\\d{16}") String number,
        @NotBlank String holder,
        @NotNull @Future LocalDate expirationDate) {}
