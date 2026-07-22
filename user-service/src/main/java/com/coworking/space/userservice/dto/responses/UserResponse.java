package com.coworking.space.userservice.dto.responses;

import java.time.LocalDate;

public record UserResponse(
        int id, String name, String surname,
        LocalDate birthDate, String email,
        boolean active) {}
