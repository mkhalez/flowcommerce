package com.coworking.space.authenticationservice.domain.statuses;

public enum RegistrationEventStatus {
    CREATED,
    PRE_SENDING,
    SENDING,
    FAIL,
    SUCCESS,
    FAIL_TO_SEND
}
