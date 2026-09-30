package com.coworking.space.orderservice.configuration;

import com.coworking.space.orderservice.properties.KafkaConsumerProperties;
import com.coworking.space.orderservice.util.ObjectIdDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.bson.types.ObjectId;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.List;
import java.util.UUID;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;
    private final KafkaConsumerProperties kafkaConsumerProperties;

    @Bean("avroConsumerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<ObjectId, Object>> avroConsumerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<ObjectId, Object>();
        factory.setConsumerFactory(
                consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setAutoStartup(kafkaConsumerProperties.isAutoStart());
        factory.setConcurrency(kafkaConsumerProperties.getConcurrentListeners());
        return factory;
    }

    @Bean
    public KafkaTemplate<UUID, Object> kafkaTemplate() {
        var props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        var factory = new DefaultKafkaProducerFactory<UUID, Object>(props);
        return new KafkaTemplate<>(factory);
    }


    private <T> ConsumerFactory<ObjectId, T> consumerFactory() {
        var props = kafkaProperties.buildConsumerProperties();

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ObjectIdDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);

        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, List.of(RoundRobinAssignor.class));

        return new DefaultKafkaConsumerFactory<>(props);
    }
}
