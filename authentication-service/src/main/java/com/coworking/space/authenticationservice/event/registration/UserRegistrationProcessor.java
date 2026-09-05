package com.coworking.space.authenticationservice.event.registration;

import com.coworking.space.authenticationservice.domain.entities.RegistrationEventEntity;
import com.coworking.space.authenticationservice.infrustructure.properties.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRegistrationProcessor {
    private final UserRegistrationDBWriter writer;
    private final KafkaTemplate<UUID, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Scheduled(fixedDelayString = "${registration.sender.scheduler.fixed.delay}")
    public void send() {
        var events = writer.markPreSendingRegistrationEntity();

        events.forEach(this::send);
    }

    private void send(RegistrationEventEntity event) {
        kafkaTemplate.send(
                kafkaTopicsProperties.getUserRegistrationTopic(),
                        event.getId(),
                        event.getPayload())
                .whenComplete((result, ex)
                        -> writer.handleResult(event.getId(), ex));
    }

}
