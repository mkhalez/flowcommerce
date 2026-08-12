package com.coworking.space.userservice.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@AllArgsConstructor
@Getter
@Validated
@ConfigurationProperties(prefix = "auth-service")
public class AuthClientProperties {
    @NotBlank
    private String baseUrl;
}
