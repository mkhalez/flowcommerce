package com.coworking.space.paymentservice.domain.entities;

import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentEntity {
    @Id
    private ObjectId id;

    private Integer orderId;

    private Integer userId;

    private PaymentStatus status;

    private Instant timestamp;

    private Instant pendingBoundary;

    private int paymentAmount;
}
