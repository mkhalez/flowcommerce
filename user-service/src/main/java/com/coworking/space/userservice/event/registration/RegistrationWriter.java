package com.coworking.space.userservice.event.registration;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.userservice.domain.entities.RegistrationProcessedEventEntity;
import com.coworking.space.userservice.domain.entities.RegistrationResultEntity;
import com.coworking.space.userservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.userservice.domain.statuses.UserRegistrationStatus;
import com.coworking.space.userservice.exception.UserAlreadyExistException;
import com.coworking.space.userservice.mapper.UserInfoMapper;
import com.coworking.space.userservice.repositories.RegistrationProcessedEventRepository;
import com.coworking.space.userservice.repositories.RegistrationResultRepository;
import com.coworking.space.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationWriter {
    private final RegistrationProcessedEventRepository registrationProcessedEventRepo;
    private final RegistrationResultRepository registrationResultRepo;
    private final UserService userService;
    private final UserInfoMapper userInfoMapper;
    private final RegistrationOutcomeWriter registrationOutcomeWriter;

    public void createUser(UserRegistrationRequest request, UUID id) {
        if(registrationResultRepo.existsById(id)) {
            return;
        }

        UserRegistrationStatus outcome;
        try {
            var userCreateRequest = userInfoMapper.toUserCreateRequest(request);
            userService.createUser(userCreateRequest);
            outcome = UserRegistrationStatus.SUCCESS;
        } catch (DataIntegrityViolationException e) {
            log.atError().setCause(e).log();
            outcome = UserRegistrationStatus.FAIL;
        } catch (UserAlreadyExistException e) {
            log.atError().setCause(e).log();
            outcome = UserRegistrationStatus.SUCCESS;
        }

        registrationOutcomeWriter.saveEvents(id, outcome);
    }
}
