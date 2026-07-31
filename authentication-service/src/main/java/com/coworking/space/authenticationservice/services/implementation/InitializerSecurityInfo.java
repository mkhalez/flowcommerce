package com.coworking.space.authenticationservice.services.implementation;

import com.coworking.space.authenticationservice.domain.entities.RoleEntity;
import com.coworking.space.authenticationservice.domain.entities.UserEntity;
import com.coworking.space.authenticationservice.properties.InitializerProperties;
import com.coworking.space.authenticationservice.repositories.RoleRepository;
import com.coworking.space.authenticationservice.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InitializerSecurityInfo implements CommandLineRunner {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final InitializerProperties initializerProperties;
    private final PasswordEncoder passwordEncoder;

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializerProperties.getRoles().forEach(role -> {
            String fullRoleName = role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role.toUpperCase();

            var roleOptional = roleRepo.findByName(fullRoleName);
            if(roleOptional.isEmpty()) {
                roleRepo.save( RoleEntity.builder()
                        .name(fullRoleName)
                        .build());
            }
        });

        RoleEntity adminRole = roleRepo.findByName(ROLE_ADMIN)
                .orElseGet(() -> roleRepo.save(RoleEntity.builder().name(ROLE_ADMIN).build()));

        if(userRepo.findByUsername(initializerProperties.getAdminUsername()).isEmpty()) {
            String encodedPassword = passwordEncoder.encode(initializerProperties.getAdminPassword());

            var adminEntity = UserEntity.builder()
                            .password(encodedPassword)
                                    .roles(Set.of(adminRole))
                                            .username(initializerProperties.getAdminUsername())
                                                    .build();
            userRepo.save(adminEntity);
        }
    }
}
