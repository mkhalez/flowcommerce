package com.coworking.space.paymentservice.domain.exceptions;

public class PaymentAlreadyProcessedException extends RuntimeException{
    public PaymentAlreadyProcessedException(String message) {
        super(message);
    }
}
