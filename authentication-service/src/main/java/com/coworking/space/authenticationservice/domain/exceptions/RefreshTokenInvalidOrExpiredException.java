package com.coworking.space.authenticationservice.domain.exceptions;

public class RefreshTokenInvalidOrExpiredException extends RuntimeException{
    public RefreshTokenInvalidOrExpiredException(String message) {
        super(message);
    }
}
