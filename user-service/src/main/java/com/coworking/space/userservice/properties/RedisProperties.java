package com.coworking.space.userservice.properties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "cache")
@Getter
@AllArgsConstructor
@Validated
public class RedisProperties {
    private int ttlInMinutes;
    @NotNull
    private String usersKey;
}
