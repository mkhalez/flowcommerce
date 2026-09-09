package com.coworking.space.paymentservice.controllers;

import com.coworking.space.paymentservice.domain.statuses.PaymentStatus;
import com.coworking.space.paymentservice.dto.response.PaymentResponse;
import com.coworking.space.paymentservice.dto.response.SumResult;
import com.coworking.space.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.OffsetDateTime;
import java.util.List;

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
        var response = paymentService.findByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> findByUserId(@PathVariable int userId) {
        var response = paymentService.findByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> findByStatus(PaymentStatus status) {
        var response = paymentService.findByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sum/{userId}")
    public ResponseEntity<SumResult> sumOfPaymentsByUserId(@RequestParam OffsetDateTime from,
                                                           @RequestParam OffsetDateTime to,
                                                           @PathVariable int userId) {
        var response = paymentService.sumOfPaymentsByUserId(userId, from, to);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sum")
    public ResponseEntity<SumResult> sumOfPayments(@RequestParam OffsetDateTime from,
                                                   @RequestParam OffsetDateTime to) {
        var response = paymentService.sumOfPayments(from, to);
        return ResponseEntity.ok(response);
    }
}
