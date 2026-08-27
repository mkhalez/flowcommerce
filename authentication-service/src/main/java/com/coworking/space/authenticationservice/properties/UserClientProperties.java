package com.coworking.space.authenticationservice.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "client.user-service")
@Validated
@AllArgsConstructor
@Getter
public class UserClientProperties {
    @NotBlank
    private String baseUrl;

    @Positive
    private int readTimeout;

    @Positive
    private int connectionTimeout;
}
