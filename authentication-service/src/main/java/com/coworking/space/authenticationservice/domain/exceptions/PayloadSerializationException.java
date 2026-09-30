package com.coworking.space.authenticationservice.domain.exceptions;

public class PayloadSerializationException extends RuntimeException {
    public PayloadSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}