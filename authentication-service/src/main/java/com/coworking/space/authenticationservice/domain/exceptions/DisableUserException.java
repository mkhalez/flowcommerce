package com.coworking.space.authenticationservice.domain.exceptions;

public class DisableUserException extends RuntimeException{
    public DisableUserException(String message) {
        super(message);
    }
}
