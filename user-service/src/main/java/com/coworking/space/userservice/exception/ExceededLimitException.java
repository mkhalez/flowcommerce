package com.coworking.space.userservice.exception;

public class ExceededLimitException extends RuntimeException{
    public ExceededLimitException(String message) {
        super(message);
    }
}
