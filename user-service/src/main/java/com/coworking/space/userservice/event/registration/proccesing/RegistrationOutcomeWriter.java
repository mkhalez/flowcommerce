package com.coworking.space.userservice.event.registration.proccesing;

import com.coworking.space.userservice.domain.entities.RegistrationProcessedEventEntity;
import com.coworking.space.userservice.domain.entities.RegistrationResultEntity;
import com.coworking.space.userservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.userservice.dto.payload.UserRegistrationPayload;
import com.coworking.space.userservice.repositories.RegistrationProcessedEventRepository;
import com.coworking.space.userservice.repositories.RegistrationResultRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationOutcomeWriter {
    private final RegistrationProcessedEventRepository registrationProcessedEventRepo;
    private final RegistrationResultRepository registrationResultRepo;
    private final ObjectMapper objectMapper;

    private static final int ZERO_ATTEMPT = 0;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEvents(UUID id, UserRegistrationPayload userRegistrationPayload) {
        var registrationResultEntity = RegistrationResultEntity.builder()
                .id(id)
                .build();
        registrationResultRepo.save(registrationResultEntity);

        String payload = "";
        RegistrationEventStatus statusOfEvent;
        try {
            payload = objectMapper.writeValueAsString(userRegistrationPayload);
            statusOfEvent = RegistrationEventStatus.CREATED;
        } catch (JacksonException e) {
            log.atError().setCause(e).log();
            statusOfEvent = RegistrationEventStatus.FAIL_TO_PARSE;
        }

        var registrationProcessedEventEntity = RegistrationProcessedEventEntity.builder()
                .id(id)
                .status(statusOfEvent)
                .nextAttemptAt(OffsetDateTime.now())
                .attemptCount(ZERO_ATTEMPT)
                .payload(payload)
                .build();

        registrationProcessedEventRepo.save(registrationProcessedEventEntity);
    }
}
