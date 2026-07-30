package com.coworking.space.authenticationservice.services.implementation;

import com.coworking.space.authenticationservice.domain.entities.UserEntity;
import com.coworking.space.authenticationservice.domain.exceptions.RoleNotFoundException;
import com.coworking.space.authenticationservice.domain.models.Role;
import com.coworking.space.authenticationservice.domain.models.User;
import com.coworking.space.authenticationservice.dto.request.UserRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;
import com.coworking.space.authenticationservice.mapers.RoleMapper;
import com.coworking.space.authenticationservice.mapers.UserMapper;
import com.coworking.space.authenticationservice.repositories.RoleRepository;
import com.coworking.space.authenticationservice.repositories.UserRepository;
import com.coworking.space.authenticationservice.services.JwtService;
import com.coworking.space.authenticationservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final String USERNAME_ALREADY_EXIST = "username already exist";
    private static final String ROLE_NOT_FOUND = "role not found";
    private static final String USER_ROLE_NAME = "ROLE_USER";

    @Override
    public AuthResponse register(UserRequest userRequest) {
        if(userRepo.existsByUsername(userRequest.getUsername())) {
            throw new BadCredentialsException(USERNAME_ALREADY_EXIST);
        }

        var roleEntity = roleRepo.findByName(USER_ROLE_NAME)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND));

        var userEntity = new UserEntity();
        userEntity.setUsername(userRequest.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userEntity.getRoles().add(roleEntity);

        var saved = userRepo.save(userEntity);

        Set<Role> roles = saved.getRoles().stream()
                .map(roleMapper::toRole)
                .collect(Collectors.toSet());
        User user = userMapper.toUser(saved, roles);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
