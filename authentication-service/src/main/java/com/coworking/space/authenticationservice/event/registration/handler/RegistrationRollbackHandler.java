package com.coworking.space.authenticationservice.event.registration.handler;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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

import java.util.UUID;

@Profile("!test")
@Component
@RequiredArgsConstructor
@KafkaListener(
        containerFactory = "avroConsumerFactory",
        topics = "${kafka.topics.user-rollback-registration-topic}"
)
@RetryableTopic(
        backOff =
        @BackOff(
                delayString = "${kafka.topics.retry-policy.backoff.delay}",
                multiplierString = "${kafka.topics.retry-policy.backoff.multiplier}"),
        attempts = "${kafka.topics.retry-policy.attempts}",
        kafkaTemplate = "kafkaAvroTemplate",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_DELAY_VALUE,
        include = {TransientDataAccessException.class, CannotCreateTransactionException.class}
)
@Slf4j
public class RegistrationRollbackHandler {
    private final RegistrationRollbackWriter registrationRollbackWriter;

    @KafkaHandler
    public void listen(UserRegistrationResult registrationResult,
                       @Header(KafkaHeaders.RECEIVED_KEY) UUID id) {
        registrationRollbackWriter.processResult(registrationResult, id);
    }
}
