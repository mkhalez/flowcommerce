package com.coworking.space.userservice.dto.payload;

import com.coworking.space.userservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.userservice.domain.statuses.UserRegistrationStatus;

public record UserRegistrationPayload(UserRegistrationStatus status, String message) {}
