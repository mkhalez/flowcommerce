package com.coworking.space.paymentservice.domain.entities;

import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payments")
public class PaymentEntity {
    @Id
    private ObjectId id;

    private Integer orderId;

    private Integer userId;

    private PaymentStatus status;

    private int paymentAmount;
}
