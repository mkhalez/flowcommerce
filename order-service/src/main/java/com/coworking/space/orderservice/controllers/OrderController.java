package com.coworking.space.orderservice.controllers;

import com.coworking.space.orderservice.dto.request.OrderFilterParams;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.services.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;

    private static final String LOCATION_OF_CREATED_RESOURCE_PATTERN = "/api/order/{id}";

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request, UriComponentsBuilder builder) {
        var response = orderService.createOrder(request);
        var location = builder.path(LOCATION_OF_CREATED_RESOURCE_PATTERN).buildAndExpand(response.getId()).toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable int id) {
        var response = orderService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(OrderFilterParams orderFilterParams, int limit, int page) {
        var response = orderService.getOrders(orderFilterParams, limit, page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<OrderResponse> findByUserId(@PathVariable int userId) {
        var response = orderService.findByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> updateById(@PathVariable int id, OrderRequest request) {
        var response = orderService.updateById(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
