package com.coworking.space.paymentservice.infrastructure.properties;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("payment.sender")
@AllArgsConstructor
@Validated
@Getter
public class PaymentSenderProperties {
    @Positive
    private int maxAttempts;
    @Positive
    private int limit;
    @Positive
    private int baseAttemptSecond;
    @Positive
    private int timeToSendMinutes;
}
