package com.coworking.space.apigateway.properties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@ConfigurationProperties(prefix = "app")
@Validated
@AllArgsConstructor
@Getter
public class SecurityProperties {
    @NotNull
    private Set<String> authenticationFreeRoutes;
}
