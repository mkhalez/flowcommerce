package com.coworking.space.authenticationservice.event.converter;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class ConverterConfiguration {
    @Bean
    public KafkaAvroSerializer kafkaAvroSerializer(KafkaProperties properties) {
        var serializer = new KafkaAvroSerializer();
        serializer.configure(properties.getProducer().getProperties(), false);
        return serializer;
    }
}
