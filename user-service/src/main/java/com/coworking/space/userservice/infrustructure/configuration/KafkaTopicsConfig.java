package com.coworking.space.userservice.infrustructure.configuration;

import com.coworking.space.userservice.infrustructure.properties.KafkaDLQTopicsProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public KafkaAdmin.NewTopics toNewTopics(KafkaDLQTopicsProperties properties) {
        var registrationTopic = new NewTopic(
                properties.getUserRegistrationName(),
                properties.getPartitions(),
                properties.getReplicas());

        var rollbackRegistrationTopic = new NewTopic(
                properties.getUserRollbackRegistrationName(),
                properties.getPartitions(),
                properties.getReplicas());

        return new KafkaAdmin.NewTopics(
                registrationTopic,
                rollbackRegistrationTopic
        );
    }
}
