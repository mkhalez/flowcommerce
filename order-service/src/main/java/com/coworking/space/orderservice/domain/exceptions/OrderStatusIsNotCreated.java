package com.coworking.space.orderservice.domain.exceptions;

public class OrderStatusIsNotCreated extends RuntimeException{
    public OrderStatusIsNotCreated(String message) {
        super(message);
    }
}
