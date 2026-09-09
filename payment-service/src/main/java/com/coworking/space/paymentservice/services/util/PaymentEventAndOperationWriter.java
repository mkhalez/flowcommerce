package com.coworking.space.paymentservice.services.util;

import com.coworking.space.paymentservice.domain.entities.PaymentEntity;
import com.coworking.space.paymentservice.domain.entities.PaymentEventEntity;
import com.coworking.space.paymentservice.domain.statuses.PaymentEventStatus;
import com.coworking.space.paymentservice.repositories.PaymentEventRepository;
import com.coworking.space.paymentservice.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentEventAndOperationWriter {
    private final PaymentRepository paymentRepo;
    private final PaymentEventRepository paymentEventRepo;

    private static final int ZERO_ATTEMPT = 0;

    @Transactional
    public void savePaymentAndEvent(PaymentEntity paymentEntity) {
        PaymentEventEntity paymentEventEntity = PaymentEventEntity.builder()
                .paymentStatus(paymentEntity.getStatus())
                .createdAt(Instant.now())
                .eventStatus(PaymentEventStatus.CREATED)
                .attemptCount(ZERO_ATTEMPT)
                .nextAttemptAt(Instant.now())
                .orderId(paymentEntity.getOrderId())
                .build();

        paymentEventRepo.save(paymentEventEntity);
        paymentRepo.save(paymentEntity);
    }
}
