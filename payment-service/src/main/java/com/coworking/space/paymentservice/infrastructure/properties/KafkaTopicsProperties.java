package com.coworking.space.paymentservice.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "kafka.topic")
@AllArgsConstructor
@Getter
@Validated
public class KafkaTopicsProperties {
    @NotBlank
    private String paymentOperationsResultTopic;
}
