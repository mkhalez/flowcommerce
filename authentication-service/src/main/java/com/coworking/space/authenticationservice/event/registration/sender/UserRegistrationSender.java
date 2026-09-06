package com.coworking.space.authenticationservice.event.registration.sender;

import com.coworking.space.authenticationservice.domain.entities.RegistrationEventEntity;
import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.authenticationservice.dto.payload.UserRegistrationPayload;
import com.coworking.space.authenticationservice.infrustructure.properties.KafkaTopicsProperties;
import com.coworking.space.authenticationservice.mapers.UserInfoMapper;
import com.coworking.space.authenticationservice.repositories.RegistrationEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationSender {
    private final UserRegistrationDBWriter writer;
    private final KafkaTemplate<UUID, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final ObjectMapper objectMapper;
    private final RegistrationEventRepository eventRepository;
    private final UserInfoMapper userInfoMapper;

    @Scheduled(fixedDelayString = "${registration.sender.scheduler.fixed.delay}")
    public void send() {
        var events = writer.markPreSendingRegistrationEntity();

        events.forEach(this::send);
    }

    private void send(RegistrationEventEntity event) {
        UserRegistrationPayload userRegistrationRequest;
        try {
            userRegistrationRequest = objectMapper.readValue(event.getPayload(), UserRegistrationPayload.class);
        } catch (JacksonException e) {
            log.atError().setCause(e).log();
            event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
            eventRepository.save(event);
            return;
        }

        UserRegistrationRequest requestAvro = userInfoMapper.toUserRegistrationRequest(userRegistrationRequest);

        kafkaTemplate.send(
                kafkaTopicsProperties.getUserRegistrationTopic(),
                        event.getId(),
                        requestAvro)
                .whenComplete((result, ex)
                        -> writer.handleResult(event.getId(), ex));
    }

}
