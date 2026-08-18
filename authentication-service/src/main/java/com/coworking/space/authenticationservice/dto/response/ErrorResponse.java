package com.coworking.space.authenticationservice.dto.response;

public class ErrorResponse extends RuntimeException {
    public ErrorResponse(String message) {
        super(message);
    }
}
