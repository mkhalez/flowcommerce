package com.coworking.space.userservice.event.registration;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.userservice.domain.entities.RegistrationProcessedEventEntity;
import com.coworking.space.userservice.domain.entities.RegistrationResultEntity;
import com.coworking.space.userservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.userservice.domain.statuses.UserRegistrationStatus;
import com.coworking.space.userservice.repositories.RegistrationProcessedEventRepository;
import com.coworking.space.userservice.repositories.RegistrationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationOutcomeWriter {
    private final RegistrationProcessedEventRepository registrationProcessedEventRepo;
    private final RegistrationResultRepository registrationResultRepo;

    private static final int ZERO_ATTEMPT = 0;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEvents(UUID id, UserRegistrationStatus outcome) {
        var registrationResultEntity = RegistrationResultEntity.builder()
                .id(id)
                .build();
        try {
            registrationResultRepo.save(registrationResultEntity);
        } catch (Exception e) {
            log.atError().setCause(e).log();
        }


        var registrationProcessedEventEntity = RegistrationProcessedEventEntity.builder()
                .id(id)
                .status(RegistrationEventStatus.CREATED)
                .nextAttemptAt(OffsetDateTime.now())
                .attemptCount(ZERO_ATTEMPT)
                .payload(outcome)
                .build();

        registrationProcessedEventRepo.save(registrationProcessedEventEntity);
    }
}
