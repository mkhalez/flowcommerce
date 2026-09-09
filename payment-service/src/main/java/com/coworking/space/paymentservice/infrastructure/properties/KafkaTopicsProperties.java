package com.coworking.space.paymentservice.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "kafka.topics")
@AllArgsConstructor
@Getter
@Validated
public class KafkaTopicsProperties {
    @NotBlank
    private String paymentOperationsResultTopic;

    @Positive
    private int partitions;

    @Positive
    private int replicas;

    @NotBlank
    private String paymentOperationsResultTopicDlq;
}
