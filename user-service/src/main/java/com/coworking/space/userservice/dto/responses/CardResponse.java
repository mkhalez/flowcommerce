package com.coworking.space.userservice.dto.responses;

import java.time.LocalDate;

public record CardResponse(
        int id, int userId, String number,
        String holder, LocalDate expirationDate,
        boolean active) {}
