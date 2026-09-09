package com.coworking.space.paymentservice.domain.entities;

import com.coworking.space.paymentservice.domain.statuses.PaymentEventStatus;
import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document("payment_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentEventEntity {
    private static final int EXPONENTIAL_BASE = 2;

    @Field("_id")
    private ObjectId id;

    @Field("payment_status")
    private PaymentStatus paymentStatus;

    @Field("order_id")
    private Integer orderId;

    @Field("created_at")
    private Instant createdAt;

    @Field("event_status")
    private PaymentEventStatus eventStatus;

    @Field("attempt_count")
    private int attemptCount;

    @Field("next_attempt_at")
    private Instant nextAttemptAt;

    @Field("sending_started_at")
    private Instant sendingStartedAt;


    public void incrementAttemptCount() {
        attemptCount++;
    }

    public void increaseNextAttemptAt(int base) {
        nextAttemptAt = nextAttemptAt.plusSeconds((int)(base * Math.pow(EXPONENTIAL_BASE, attemptCount)));
    }
}
