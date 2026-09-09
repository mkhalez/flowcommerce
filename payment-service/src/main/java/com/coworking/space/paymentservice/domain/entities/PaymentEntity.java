package com.coworking.space.paymentservice.domain.entities;

import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document("payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentEntity {
    @Id
    @Field("_id")
    private ObjectId id;

    @Field("order_id")
    private Integer orderId;

    @Field("user_id")
    private Integer userId;

    @Field("status")
    private PaymentStatus status;

    @Field("timestamp")
    private Instant timestamp;

    @Field("pending_boundary")
    private Instant pendingBoundary;

    @Field("payment_amount")
    private double paymentAmount;
}
