package com.coworking.space.authenticationservice.domain.exceptions;

public class FailUserRegistration extends RuntimeException{
    public FailUserRegistration(String message) {
        super(message);
    }
}
