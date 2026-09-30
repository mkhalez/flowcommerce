package com.coworking.space.userservice.event.registration.sending;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationResult;
import com.coworking.space.userservice.domain.entities.RegistrationProcessedEventEntity;
import com.coworking.space.userservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.userservice.dto.payload.UserRegistrationPayload;
import com.coworking.space.userservice.infrustructure.properties.KafkaTopicsProperties;
import com.coworking.space.userservice.mapper.ResultRegistrationMapper;
import com.coworking.space.userservice.repositories.RegistrationProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
@Profile("!test")
@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationResultSender {
    private final RegistrationResultWriter writer;
    private final KafkaTemplate<UUID, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final RegistrationProcessedEventRepository registrationEventRepository;
    private final ResultRegistrationMapper resultRegistrationMapper;
    private final KafkaTopicsProperties kafkaTopicsProperties;


    @Scheduled(fixedDelayString = "${registration.sender.scheduler.fixed.delay}")
    public void send() {
        var events = writer.markPreSendingRegistrationEntity();

        events.forEach(this::send);
    }

    private void send(RegistrationProcessedEventEntity event) {
        UserRegistrationPayload userRegistrationRequest;
        try {
            userRegistrationRequest = objectMapper.readValue(event.getPayload(), UserRegistrationPayload.class);
        } catch (JacksonException e) {
            log.atError().setCause(e).log();
            event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
            registrationEventRepository.save(event);
            return;
        }

        UserRegistrationResult requestAvro = resultRegistrationMapper.toUserRegistrationResultAvro(userRegistrationRequest);

        kafkaTemplate.send(
                        kafkaTopicsProperties.getUserRollbackRegistrationTopic(),
                        event.getId(),
                        requestAvro)
                .whenComplete((result, ex)
                        -> writer.handleResult(event.getId(), ex));
    }
}
