package com.coworking.space.orderservice.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "user-service")
@Validated
@AllArgsConstructor
@Getter
public class ClientProperties {
    @NotBlank
    private String baseUrl;

    @Positive
    private int connectionTimeout;

    @Positive
    private int readTimeout;
}
