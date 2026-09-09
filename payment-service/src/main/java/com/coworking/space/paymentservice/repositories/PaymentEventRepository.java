package com.coworking.space.paymentservice.repositories;

import com.coworking.space.paymentservice.domain.entities.PaymentEntity;
import com.coworking.space.paymentservice.domain.entities.PaymentEventEntity;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface PaymentEventRepository {
    PaymentEventEntity save(PaymentEventEntity entity);

    List<PaymentEventEntity> entitiesToSend();

    Optional<PaymentEventEntity> findById(ObjectId id);
}
