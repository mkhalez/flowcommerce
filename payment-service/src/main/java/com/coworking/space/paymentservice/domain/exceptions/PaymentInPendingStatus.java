package com.coworking.space.paymentservice.domain.exceptions;

public class PaymentInPendingStatus extends RuntimeException{
    public PaymentInPendingStatus(String message) {
        super(message);
    }
}
