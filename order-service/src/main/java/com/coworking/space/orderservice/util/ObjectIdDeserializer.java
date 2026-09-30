package com.coworking.space.orderservice.util;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.bson.types.ObjectId;

import java.nio.charset.StandardCharsets;

public class ObjectIdDeserializer implements Deserializer<ObjectId> {
    @Override
    public ObjectId deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        String hexString = new String(data, StandardCharsets.UTF_8);
        return new ObjectId(hexString);
    }
}
