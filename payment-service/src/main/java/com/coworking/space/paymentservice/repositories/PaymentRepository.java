package com.coworking.space.paymentservice.repositories;

import com.coworking.space.paymentservice.domain.entities.PaymentEntity;
import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    PaymentEntity save(PaymentEntity entity);

    List<PaymentEntity> findByUserId(int userId);

    List<PaymentEntity> findByOrderId(int orderId);

    List<PaymentEntity> findByStatus(PaymentStatus status);

    double getSumByUser(Instant from, Instant to, int userId);

    double getTotalSum(Instant from, Instant to);

    boolean existsById(ObjectId id);
}
