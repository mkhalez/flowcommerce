package com.coworking.space.authenticationservice.event.properties;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "registration.sender")
@AllArgsConstructor
@Validated
@Getter
public class SenderProperties {
    @Positive
    private int maxAttempts;
    @Positive
    private int numberEventsToProcess;
    @Positive
    private int kafkaClusterTimeoutSS;
    @Positive
    private int baseAttemptSecondAdder;
}
