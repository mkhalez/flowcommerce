package com.coworking.space.paymentservice.events.payment;

import com.coworking.space.paymentservice.domain.entities.PaymentEventEntity;
import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import com.coworking.space.paymentservice.dto.broker.avro.PaymentOrderEvent;
import com.coworking.space.paymentservice.dto.broker.avro.RegistrationStatus;
import com.coworking.space.paymentservice.infrastructure.properties.KafkaTopicsProperties;
import com.coworking.space.paymentservice.repositories.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventSender {
    private final PaymentEventWriter paymentEventWriter;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Scheduled(fixedDelayString = "${payment.sender.scheduler.fixed.delay}")
    public void send() {
        var entities = paymentEventWriter.entitiesToSend();

        entities.forEach(this::send);
    }

    private void send(PaymentEventEntity event) {
        RegistrationStatus registrationStatus = event.getPaymentStatus() == PaymentStatus.SUCCESS
                ? RegistrationStatus.SUCCESS
                : RegistrationStatus.FAIL;
        PaymentOrderEvent paymentOrderEvent = new PaymentOrderEvent(event.getOrderId(), registrationStatus);

        kafkaTemplate.send(
                        kafkaTopicsProperties.getPaymentOperationsResultTopic(),
                        event.getId(),
                        paymentOrderEvent)
                .whenComplete((result, ex)
                        -> paymentEventWriter.handleResult(event.getId(), ex));
    }

}
