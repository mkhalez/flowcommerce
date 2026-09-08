package com.coworking.space.paymentservice.services;

import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import com.coworking.space.paymentservice.dto.response.SumResult;
import com.coworking.space.paymentservice.dto.response.PaymentResponse;

import java.time.OffsetDateTime;
import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(int orderId);

    PaymentResponse findByOrderId(int orderId);

    List<PaymentResponse> findByUserId(int userId);

    List<PaymentResponse> findByStatus(PaymentStatus status);

    SumResult sumOfPaymentsByUserId(int userId, OffsetDateTime from, OffsetDateTime to);

    SumResult sumOfPayments(OffsetDateTime from, OffsetDateTime to);
}
