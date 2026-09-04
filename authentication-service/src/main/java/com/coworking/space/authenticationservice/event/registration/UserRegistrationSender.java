package com.coworking.space.authenticationservice.event.registration;

import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.authenticationservice.event.properties.SenderProperties;
import com.coworking.space.authenticationservice.infrustructure.properties.KafkaTopicsProperties;
import com.coworking.space.authenticationservice.repositories.RegistrationEventRepository;
import io.confluent.common.utils.Time;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class UserRegistrationSender {
    private final RegistrationEventRepository registrationEventRepo;
    private final SenderProperties senderProperties;
    private final KafkaTemplate<UUID, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    private static final int START_PAGE = 0;

    @Scheduled(fixedDelayString = "${registration.sender.scheduler.fixed.delay}")
    public void send() {
        var events = registrationEventRepo.findEventToProcess(
                OffsetDateTime.now(),
                senderProperties.getMaxAttempts(),
                PageRequest.of(START_PAGE, senderProperties.getNumberEventsToProcess()));


        for(var event : events) {
            if(event.getAttemptCount() > senderProperties.getMaxAttempts()) {
                event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
                continue;
            }

            try {
                kafkaTemplate.send(
                        kafkaTopicsProperties.getUserRegistrationTopic(),
                        event.getId(),
                        event.getPayload()
                ).get(senderProperties.getKafkaClusterTimeoutSS(), TimeUnit.SECONDS);
                event.setStatus(RegistrationEventStatus.PENDING);
                registrationEventRepo.save(event);
            } catch (ExecutionException | TimeoutException e) {
                if(e.getCause() instanceof RetriableException || e instanceof TimeoutException) {
                    event.incrementAttemptCount();
                    event.increaseNextAttemptAt(senderProperties.getBaseAttemptSecondAdder() * event.getAttemptCount());
                } else {
                    event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
                }
                registrationEventRepo.save(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
                registrationEventRepo.save(event);
            }

        }
    }
}
