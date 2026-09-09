package com.coworking.space.orderservice.event.payment;

import com.coworking.space.orderservice.dto.broker.avro.PaymentOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

@Component
@RequiredArgsConstructor
@KafkaListener(
        containerFactory = "avroConsumerFactory",
        topics = "${kafka.payment-operations-result-topic}"
)
@RetryableTopic(
        backOff =
        @BackOff(
                delayString = "${kafka.topics.retry-policy.backoff.delay}",
                multiplierString = "${kafka.topics.retry-policy.backoff.multiplier}"),
        attempts = "${kafka.topics.retry-policy.attempts}",
        kafkaTemplate = "kafkaTemplate",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_DELAY_VALUE,
        include = {TransientDataAccessException.class, CannotCreateTransactionException.class}
)
@Slf4j
public class PaymentEventHandler {
    private final PaymentEventWriter paymentEventWriter;

    @KafkaHandler
    public void listen(PaymentOrderEvent event,
                       @Header(KafkaHeaders.RECEIVED_KEY) ObjectId id) {
        paymentEventWriter.changeOrderStatus(event, id.toHexString());
    }
}
