package com.coworking.space.authenticationservice.domain.models;

import lombok.Data;

import java.util.Set;

@Data
public class User {
    private String username;

    private String password;

    private Set<Role> roles;
}
