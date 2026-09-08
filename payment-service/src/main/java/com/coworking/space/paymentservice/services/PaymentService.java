package com.coworking.space.paymentservice.services;

import com.coworking.space.paymentservice.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(int orderId);


}
