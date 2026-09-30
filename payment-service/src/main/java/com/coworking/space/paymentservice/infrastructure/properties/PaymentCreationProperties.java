package com.coworking.space.paymentservice.infrastructure.properties;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "payment-creation")
@Validated
@AllArgsConstructor
@Getter
public class PaymentCreationProperties {
    @Positive
    private int minutesDelta;
}
