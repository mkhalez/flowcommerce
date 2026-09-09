package com.coworking.space.authenticationservice.event.registration.sender;

import com.coworking.space.authenticationservice.domain.entities.RegistrationEventEntity;
import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.authenticationservice.event.properties.SenderProperties;
import com.coworking.space.authenticationservice.repositories.RegistrationEventRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Profile("!test")
@Service
@RequiredArgsConstructor
public class UserRegistrationDBWriter {
    private final RegistrationEventRepository eventRepository;
    private final SenderProperties properties;

    @Transactional
    public List<RegistrationEventEntity> markPreSendingRegistrationEntity() {
        var events = eventRepository.findEventToProcess(
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

        eventRepository.saveAll(events);
        return events;
    }

    @Transactional
    public void handleResult(UUID id, Throwable e) {
        var event = eventRepository.findById(id).orElse(null);
        if(event == null) return;

        if(e == null) {
            event.setStatus(RegistrationEventStatus.SENDING);
        } else if(isRetriable(e)){
            event.increaseNextAttemptAt(properties.getBaseAttemptSecond());
            event.setStatus(RegistrationEventStatus.CREATED);
        } else {
            event.setStatus(RegistrationEventStatus.FAIL_TO_SEND);
        }

        eventRepository.save(event);
    }

    private boolean isRetriable(Throwable e) {
        return e instanceof RetriableException
                || e.getCause() instanceof RetriableException;
    }
}
