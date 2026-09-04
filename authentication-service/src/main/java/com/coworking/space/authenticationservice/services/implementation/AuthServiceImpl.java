package com.coworking.space.authenticationservice.services.implementation;

import com.coworking.space.authenticationservice.clients.UserClient;
import com.coworking.space.authenticationservice.domain.entities.RegistrationEventEntity;
import com.coworking.space.authenticationservice.domain.entities.UserEntity;
import com.coworking.space.authenticationservice.domain.exceptions.*;
import com.coworking.space.authenticationservice.domain.models.Role;
import com.coworking.space.authenticationservice.domain.models.User;
import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.authenticationservice.dto.request.LoginRequest;
import com.coworking.space.authenticationservice.dto.request.RefreshRequest;
import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;
import com.coworking.space.authenticationservice.dto.response.RegistrationStatusResponse;
import com.coworking.space.authenticationservice.mapers.RegistrationMapper;
import com.coworking.space.authenticationservice.mapers.RoleMapper;
import com.coworking.space.authenticationservice.mapers.UserMapper;
import com.coworking.space.authenticationservice.mapers.UserRequestMapper;
import com.coworking.space.authenticationservice.repositories.RegistrationEventRepository;
import com.coworking.space.authenticationservice.repositories.RoleRepository;
import com.coworking.space.authenticationservice.repositories.UserRepository;
import com.coworking.space.authenticationservice.services.JwtService;
import com.coworking.space.authenticationservice.services.AuthService;
import com.coworking.space.authenticationservice.utils.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtDecoder decoder;
    private final AuthenticationManager authenticationManager;
    private final RegistrationEventRepository registrationEventRepo;
    private final RegistrationMapper registrationMapper;
    private final UserClient userClient;
    private final UserRequestMapper userRequestMapper;

    private static final String USERNAME_ALREADY_EXIST = "username already exist";
    private static final String USERNAME_NOT_FOUND = "username not found";
    private static final String REGISTRATION_NOT_FOUND = "registration not found";
    private static final String REFRESH_TOKEN_ERROR = "refresh token invalid or expired";
    private static final String ROLE_NOT_FOUND = "role not found";
    private static final String USER_ROLE_NAME = "ROLE_USER";
    private static final String REFRESH_TYPE = "refresh";
    private static final String TOKEN_TYPE_NAME = "type";
    private static final String USER_IS_DISABLED = "user is disabled";
    private static final boolean INIT_DEACTIVE = false;
    private static final int ZERO_ATTEMPT = 0;

    @Override
    @Transactional
    public RegistrationStatusResponse register(SingUpRequest userRequest, byte[] payload) {
        if(userRepo.existsByUsername(userRequest.getUsername())) {
            throw new UserAlreadyExistException(USERNAME_ALREADY_EXIST);
        }

        var roleEntity = roleRepo.findByName(USER_ROLE_NAME)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND));
        var userEntity = UserEntity.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .roles(Set.of(roleEntity))
                .active(INIT_DEACTIVE)
                .build();
        var savedUser = userRepo.save(userEntity);

        var registrationEventEntity = RegistrationEventEntity.builder()
                .user(savedUser)
                .payload(payload)
                .status(RegistrationEventStatus.CREATED)
                .attemptCount(ZERO_ATTEMPT)
                .nextAttemptAt(OffsetDateTime.now())
                .build();

        var savedRegistrationEvent = registrationEventRepo.save(registrationEventEntity);
        return RegistrationStatusResponse.builder()
                .transactionId(savedRegistrationEvent.getId())
                .status(savedRegistrationEvent.getStatus())
                .build();
    }

//        @Override
//    public AuthResponse register(SingUpRequest userRequest) {
//        if(userRepo.existsByUsername(userRequest.getUsername())) {
//            throw new UserAlreadyExistException(USERNAME_ALREADY_EXIST);
//        }
//
//        var roleEntity = roleRepo.findByName(USER_ROLE_NAME)
//                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND));
//
//        var userEntity = UserEntity.builder()
//                .username(userRequest.getUsername())
//                .password(passwordEncoder.encode(userRequest.getPassword()))
//                .roles(Set.of(roleEntity))
//                .active(INIT_DEACTIVE)
//                .build();
//
//        var saved = userRepo.save(userEntity);
//        try {
//            var userInfo = userRequestMapper.toUserServiceRequest(userRequest.getUserInfo(), saved.getId());
//            userClient.createUser(userInfo);
//            Set<Role> roles = saved.getRoles().stream()
//                    .map(roleMapper::toRole)
//                    .collect(Collectors.toSet());
//            saved.setActive(INIT_ACTIVE);
//            saved = userRepo.save(saved);
//
//            User user = userMapper.toUser(saved, roles);
//            String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRoles(), user.getId());
//            String refreshToken = jwtService.generateRefreshToken(user.getUsername());
//
//            log.atInfo().addKeyValue("event", "registration")
//                    .addKeyValue("username", userRequest.getUsername())
//                    .log();
//
//            return AuthResponse.builder()
//                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .build();
//        } catch (Exception e) {
//            log.atError().setCause(e).log();
//            try {
//                userRepo.deleteById(saved.getId());
//            } catch (Exception ex) {
//                log.error("Failed to cleanup user after external client failure: {}", saved.getId(), ex);
//            }
//            throw new FailUserRegistration(FAIL_USER_REGISTRATION_ERROR);
//        }
//    }

    @Override
    public AuthResponse authenticate(LoginRequest userRequest) {
        var token = new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        var userDetails = (CustomUserDetails) authentication.getPrincipal();

        Boolean active = userDetails.getActive();
        if(active != null && !active) {
            throw new DisableUserException(USER_IS_DISABLED);
        }

        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::new)
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(userDetails.getUsername(), roles, userDetails.getId());
        String refreshToken = jwtService.generateRefreshToken(authentication.getName());

        log.atInfo().addKeyValue("event", "authentication")
                .addKeyValue("username", userRequest.getUsername())
                .addKeyValue("accessToken", accessToken)
                .addKeyValue("refreshToken", refreshToken)
                .log();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse accessToken(RefreshRequest refreshRequest) {
        String refreshTokenFromRequest = refreshRequest.getRefreshToken();
        Jwt jwt;
        try {
            jwt = decoder.decode(refreshTokenFromRequest);
        } catch (JwtException e) {
            log.atError().setCause(e).log();

            throw new RefreshTokenInvalidOrExpiredException(REFRESH_TOKEN_ERROR);
        }

        String type = jwt.getClaim(TOKEN_TYPE_NAME);

        if(type == null || !type.equals(REFRESH_TYPE)) {
            throw new RefreshTokenInvalidOrExpiredException(REFRESH_TOKEN_ERROR);
        }

        String username = jwt.getSubject();
        var entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND));

        if(!entity.getActive()) {
            throw new DisableUserException(USER_IS_DISABLED);
        }

        Set<Role> roles = entity.getRoles().stream()
                .map(roleMapper::toRole)
                .collect(Collectors.toSet());
        User user = userMapper.toUser(entity, roles);

        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRoles(), user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        log.atInfo().addKeyValue("event", "refresh token")
                .addKeyValue("username", user.getUsername())
                .addKeyValue("accessToken", accessToken)
                .addKeyValue("refreshToken", refreshToken)
                .log();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public RegistrationStatusResponse checkRegistrationStatus(UUID registrationId) {
        var entity = registrationEventRepo.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException(REGISTRATION_NOT_FOUND));

        return registrationMapper.toRegistrationStatusResponse(entity);
    }
}
