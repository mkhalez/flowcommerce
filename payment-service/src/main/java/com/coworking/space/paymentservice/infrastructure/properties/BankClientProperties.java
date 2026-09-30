package com.coworking.space.paymentservice.infrastructure.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "bank-client")
@Validated
@AllArgsConstructor
@Getter
public class BankClientProperties {
    @NotBlank
    private String baseUrl;

    @Valid
    private Body body;

    @Positive
    private int connectionTimeout;

    @Positive
    private int readTimeout;

    @Valid
    @AllArgsConstructor
    @Getter
    public static class Body {
        @NotBlank
        private String jsonrpc;
        @NotBlank
        private String method;

        @Positive
        private int id;

        @Valid
        private Params params;

        @Getter
        @AllArgsConstructor
        public static class Params {
            @Positive
            private int n;
            @Positive
            private int min;
            @Positive
            private int max;
            @NotBlank
            private String apiKey;
        }
    }
}
