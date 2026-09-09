package com.coworking.space.authenticationservice.event.registration.handler;

import com.coworking.space.authenticationservice.domain.entities.RollbackRegistrationResultEntity;
import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.authenticationservice.dto.broker.avro.RegistrationStatus;
import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationResult;
import com.coworking.space.authenticationservice.repositories.RegistrationEventRepository;
import com.coworking.space.authenticationservice.repositories.RollbackRegistrationResultRepository;
import com.coworking.space.authenticationservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class RegistrationRollbackWriter {
    private final UserRepository userRepo;
    private final RegistrationEventRepository registrationEventRepo;
    private final RollbackRegistrationResultRepository rollbackRegistrationResultRepo;

    private static final boolean ACTIVE_USER_STATUS = true;

    @Transactional
    public void processResult(UserRegistrationResult registrationResult, UUID id) {
        if(rollbackRegistrationResultRepo.existsById(id)) {
            return;
        }

        var registrationEvent = registrationEventRepo.findById(id);
        if(registrationEvent.isEmpty()) {
            return;
        }

        var rollbackRegistrationResultEntity = RollbackRegistrationResultEntity.builder()
                .id(id)
                .message(registrationResult.getMessage().toString())
                .build();
        rollbackRegistrationResultRepo.save(rollbackRegistrationResultEntity);

        var registration = registrationEvent.get();
        var user = registration.getUser();

        if(user == null) {
            registration.setStatus(RegistrationEventStatus.FAIL);
            registrationEventRepo.save(registration);
            return;
        }

        if(registrationResult.getStatus() == RegistrationStatus.SUCCESS) {
            user.setActive(ACTIVE_USER_STATUS);
            userRepo.save(user);

            registration.setStatus(RegistrationEventStatus.SUCCESS);
        } else {
            userRepo.delete(user);
            registration.setStatus(RegistrationEventStatus.FAIL);
            registration.setUser(null);
        }
        registrationEventRepo.save(registration);
    }

}
