package com.coworking.space.authenticationservice.domain.exceptions;

public class RegistrationNotFoundException extends RuntimeException {
    public RegistrationNotFoundException(String message) {
        super(message);
    }
}
