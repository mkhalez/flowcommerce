package com.coworking.space.userservice.event.registration.proccesing;

import com.coworking.space.authenticationservice.dto.broker.avro.UserRegistrationRequest;
import com.coworking.space.userservice.domain.statuses.UserRegistrationStatus;
import com.coworking.space.userservice.dto.payload.UserRegistrationPayload;
import com.coworking.space.userservice.exception.DuplicateEmailException;
import com.coworking.space.userservice.exception.UserAlreadyExistException;
import com.coworking.space.userservice.mapper.UserInfoMapper;
import com.coworking.space.userservice.repositories.RegistrationProcessedEventRepository;
import com.coworking.space.userservice.repositories.RegistrationResultRepository;
import com.coworking.space.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

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
    private final String SUCCESS_MESSAGE = "create user";

    public void createUser(UserRegistrationRequest request, UUID id) {
        if(registrationResultRepo.existsById(id)) {
            return;
        }

        UserRegistrationStatus status;
        String message;
        try {
            var userCreateRequest = userInfoMapper.toUserCreateRequest(request);
            userService.createUser(userCreateRequest);
            status = UserRegistrationStatus.SUCCESS;
            message = SUCCESS_MESSAGE;
        } catch (DataIntegrityViolationException | DuplicateEmailException e) {
            log.atError().setCause(e).log();
            status = UserRegistrationStatus.FAIL;
            message = e.getMessage();
        } catch (UserAlreadyExistException e) {
            log.atError().setCause(e).log();
            status = UserRegistrationStatus.SUCCESS;
            message = e.getMessage();
        }

        registrationOutcomeWriter.saveEvents(
                id,
                new UserRegistrationPayload(status, message));
    }
}
