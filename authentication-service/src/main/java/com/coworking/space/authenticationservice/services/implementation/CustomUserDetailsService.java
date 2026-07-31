package com.coworking.space.authenticationservice.services.implementation;

import com.coworking.space.authenticationservice.domain.entities.RoleEntity;
import com.coworking.space.authenticationservice.domain.entities.UserEntity;
import com.coworking.space.authenticationservice.repositories.UserRepository;
import com.coworking.space.authenticationservice.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    private final static String USERNAME_NOT_FOUND_ERROR = "username not found";

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_ERROR));

        var authorities = entity.getRoles().stream()
                .map(RoleEntity::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return CustomUserDetails.builder()
                .id(entity.getId())
                .password(entity.getPassword())
                .username(entity.getUsername())
                .authorities(authorities)
                .build();
    }
}
