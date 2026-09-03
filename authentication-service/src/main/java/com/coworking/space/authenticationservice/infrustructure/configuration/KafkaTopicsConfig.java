package com.coworking.space.authenticationservice.infrustructure.configuration;

import com.coworking.space.authenticationservice.infrustructure.properties.KafkaTopicsProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public KafkaAdmin.NewTopics toNewTopics(KafkaTopicsProperties properties) {
        var registrationTopic = new NewTopic(
                properties.getUserRegistrationTopic(),
                properties.getPartitions(),
                properties.getReplicas());

        var rollbackRegistrationTopic = new NewTopic(
                properties.getUserRollbackRegistrationTopic(),
                properties.getPartitions(),
                properties.getReplicas());

        return new KafkaAdmin.NewTopics(
                registrationTopic,
                rollbackRegistrationTopic
        );
    }
}
