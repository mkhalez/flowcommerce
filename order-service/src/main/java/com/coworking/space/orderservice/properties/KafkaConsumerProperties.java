package com.coworking.space.orderservice.properties;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@Profile("!test")
@ConfigurationProperties(prefix = "kafka")
@AllArgsConstructor
@Getter
@Validated
public class KafkaConsumerProperties {
    @Positive
    private int concurrentListeners;

    private boolean autoStart;
}
