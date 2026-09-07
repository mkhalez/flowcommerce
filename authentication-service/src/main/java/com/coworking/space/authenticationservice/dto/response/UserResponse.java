package com.coworking.space.authenticationservice.dto.response;

import java.time.LocalDate;

public record UserResponse(
        int id, String name, String surname,
        LocalDate birthDay, String email,
        int cardsCount, boolean active) {}
