package com.coworking.space.paymentservice.events.payment;

import com.coworking.space.paymentservice.domain.entities.PaymentEntity;
import com.coworking.space.paymentservice.domain.entities.PaymentEventEntity;
import com.coworking.space.paymentservice.domain.statuses.PaymentEventStatus;
import com.coworking.space.paymentservice.infrastructure.properties.PaymentSenderProperties;
import com.coworking.space.paymentservice.repositories.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.RetriableException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventWriter {
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentSenderProperties properties;

    public List<PaymentEventEntity> entitiesToSend() {
        return paymentEventRepository.entitiesToSend();
    }

    public List<PaymentEventEntity> incrementAttemptCount(List<Integer> ids) {

    }

    @Transactional
    public void handleResult(ObjectId id, Throwable e) {
        var event = paymentEventRepository.findById(id).orElse(null);
        if(event == null) return;

        if(e == null) {
            event.setEventStatus(PaymentEventStatus.SUCCESS);
        } else if(isRetriable(e)){
            event.increaseNextAttemptAt(properties.getBaseAttemptSecond());
            event.setEventStatus(PaymentEventStatus.CREATED);
        } else {
            event.setEventStatus(PaymentEventStatus.FAIL);
        }

        paymentEventRepository.save(event);
    }

    private boolean isRetriable(Throwable e) {
        return e instanceof RetriableException
                || e.getCause() instanceof RetriableException;
    }
}
