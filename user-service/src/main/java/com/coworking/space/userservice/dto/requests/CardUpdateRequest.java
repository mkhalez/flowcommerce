package com.coworking.space.userservice.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CardUpdateRequest(
        String holder,
        @Future LocalDate expirationDate) {}
