package com.coworking.space.apigateway.properties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Set;

@ConfigurationProperties(prefix = "app")
@AllArgsConstructor
@Getter
public class SecurityProperties {
    private Set<String> permitAllRoutes;

    private Set<String> authenticatedRoutes;
}
