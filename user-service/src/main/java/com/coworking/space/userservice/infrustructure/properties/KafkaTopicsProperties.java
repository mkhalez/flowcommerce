package com.coworking.space.userservice.infrustructure.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@Profile("!test")
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@Validated
@AllArgsConstructor
public class KafkaTopicsProperties {
    @NotBlank
    private String userRollbackRegistrationTopic;
}
