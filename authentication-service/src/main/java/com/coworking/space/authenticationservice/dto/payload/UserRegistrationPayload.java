package com.coworking.space.authenticationservice.dto.payload;

import java.time.LocalDate;

public record UserRegistrationPayload(
        Integer authId,
        String name,
        String surname,
        String email,
        LocalDate birthDay
) {}
