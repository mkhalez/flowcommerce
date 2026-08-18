package com.coworking.space.authenticationservice.properties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "security")
@Validated
@AllArgsConstructor
@Getter
public class AuthSecurityProperties {
    @NotNull
    private String issuer;

    private int accessTokenMinutes;

    private int refreshTokenMinutes;
}
