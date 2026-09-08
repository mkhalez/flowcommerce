package com.coworking.space.paymentservice.controllers;

import com.coworking.space.paymentservice.dto.response.PaymentResponse;
import com.coworking.space.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    private static final String LOCATION_OF_CREATED_PAYMENT_RESOURCE_PATTERN = "/api/payment/{orderId}";

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> createPayment(@PathVariable int orderId, UriComponentsBuilder builder) {
        var response = paymentService.createPayment(orderId);
        var location = builder.path(LOCATION_OF_CREATED_PAYMENT_RESOURCE_PATTERN).buildAndExpand(response.getOrderId()).toUri();

        return ResponseEntity.created(location)
                .body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> findByOrderId(@PathVariable int orderId) {

    }
}
