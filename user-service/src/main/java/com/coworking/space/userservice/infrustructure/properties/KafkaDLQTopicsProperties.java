package com.coworking.space.userservice.infrustructure.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@Profile("!test")
@ConfigurationProperties(prefix = "kafka.topics.dlq")
@Getter
@Validated
@AllArgsConstructor
public class KafkaDLQTopicsProperties {
    @NotBlank
    private String userRegistrationName;
    @NotBlank
    private String userRollbackRegistrationName;
    @Positive
    private int partitions;
    @Positive
    private short replicas;
}
