package com.coworking.space.authenticationservice.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@ConfigurationProperties(prefix = "security.initial")
@Validated
@AllArgsConstructor
@Getter
public class InitializerProperties {
    @NotBlank
    private String adminPassword;

    @NotBlank
    private String adminUsername;

    private Set<String> roles;

    @NotNull
    private Boolean active;
}
