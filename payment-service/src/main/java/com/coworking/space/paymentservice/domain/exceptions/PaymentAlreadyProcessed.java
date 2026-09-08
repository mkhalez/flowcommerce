package com.coworking.space.paymentservice.domain.exceptions;

public class PaymentAlreadyProcessed extends RuntimeException{
    public PaymentAlreadyProcessed(String message) {
        super(message);
    }
}
