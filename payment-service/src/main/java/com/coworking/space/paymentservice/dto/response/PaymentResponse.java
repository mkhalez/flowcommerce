package com.coworking.space.paymentservice.dto.response;

import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class PaymentResponse {
    private ObjectId id;

    private Integer orderId;

    private Integer userId;

    private PaymentStatus status;

    private Instant timestamp;

    private int paymentAmount;
}
