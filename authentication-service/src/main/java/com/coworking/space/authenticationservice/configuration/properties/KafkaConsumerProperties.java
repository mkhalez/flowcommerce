package com.coworking.space.authenticationservice.configuration.properties;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "kafka")
@AllArgsConstructor
@Getter
@Validated
public class KafkaConsumerProperties {
    @Positive
    private int concurrentListeners;

    private boolean autoStart;
}