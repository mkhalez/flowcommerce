package com.coworking.space.paymentservice.infrastructure.configuration;

import com.coworking.space.paymentservice.infrastructure.properties.KafkaTopicsProperties;
import com.coworking.space.paymentservice.infrastructure.util.ObjectIdSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.bson.types.ObjectId;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {
    @Bean
    public KafkaTemplate<ObjectId, Object> kafkaTemplate(KafkaProperties kafkaProperties) {
        var props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ObjectIdSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        var factory = new DefaultKafkaProducerFactory<ObjectId, Object>(props);
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public KafkaAdmin.NewTopics toNewTopics(KafkaTopicsProperties properties) {
        var paymentOperationResultTopic = new NewTopic(
                properties.getPaymentOperationsResultTopic(),
                properties.getPartitions(),
                (short)properties.getReplicas());

        var paymentOperationResultTopicDlq = new NewTopic(
                properties.getPaymentOperationsResultTopicDlq(),
                properties.getPartitions(),
                (short)properties.getReplicas());

        return new KafkaAdmin.NewTopics(
                paymentOperationResultTopic,
                paymentOperationResultTopicDlq
        );
    }
}
