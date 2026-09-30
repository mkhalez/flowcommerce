package com.coworking.space.userservice.event.registration.sending;

import com.coworking.space.userservice.domain.entities.RegistrationProcessedEventEntity;
import com.coworking.space.userservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.userservice.event.registration.properties.SenderProperties;
import com.coworking.space.userservice.repositories.RegistrationProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationResultWriter {
    private final RegistrationProcessedEventRepository registrationProcessedEventRepository;
    private final SenderProperties properties;

    public List<RegistrationProcessedEventEntity> markPreSendingRegistrationEntity() {
        var events = registrationProcessedEventRepository.findEventToProcess(
                OffsetDateTime.now(),
                OffsetDateTime.now().minusMinutes(properties.getTimeToSendMinutes()),
                properties.getMaxAttempts(),
                properties.getLimit()
        );

        for(var event : events) {
            if(event.getAttemptCount() == properties.getMaxAttempts()) {
                event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
            } else {
                event.setStatus(RegistrationEventStatus.PRE_SENDING);
                event.setSendingStartedAt(OffsetDateTime.now());
                event.incrementAttemptCount();
            }
        }

        registrationProcessedEventRepository.saveAll(events);
        return events;
    }

    @Transactional
    public void handleResult(UUID id, Throwable e) {
        var event = registrationProcessedEventRepository.findById(id).orElse(null);
        if(event == null) return;

        if(e == null) {
            event.setStatus(RegistrationEventStatus.SEND);
        } else if(isRetriable(e)){
            event.increaseNextAttemptAt(properties.getBaseAttemptSecond());
            event.setStatus(RegistrationEventStatus.CREATED);
        } else {
            event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
        }

        registrationProcessedEventRepository.save(event);
    }

    private boolean isRetriable(Throwable e) {
        return e instanceof RetriableException
                || e.getCause() instanceof RetriableException;
    }
}
