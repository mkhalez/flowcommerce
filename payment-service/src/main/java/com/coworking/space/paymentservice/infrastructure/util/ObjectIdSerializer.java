package com.coworking.space.paymentservice.infrastructure.util;

import org.apache.kafka.common.serialization.Serializer;
import org.bson.types.ObjectId;

import java.nio.charset.StandardCharsets;

public class ObjectIdSerializer implements Serializer<ObjectId> {
    @Override
    public byte[] serialize(String topic, ObjectId data) {
        if(data == null) {
            return null;
        }

        return data.toHexString().getBytes(StandardCharsets.UTF_8);
    }
}
