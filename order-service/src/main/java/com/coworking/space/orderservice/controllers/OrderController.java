package com.coworking.space.orderservice.controllers;

import com.coworking.space.orderservice.dto.request.OrderFilterParams;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderStatusRequest;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.facades.OrderServiceFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderServiceFacade orderServiceFacade;

    private static final String LOCATION_OF_CREATED_RESOURCE_PATTERN = "/api/order/{id}";

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request, UriComponentsBuilder builder) {
        var response = orderServiceFacade.createOrder(request);
        var location = builder.path(LOCATION_OF_CREATED_RESOURCE_PATTERN).buildAndExpand(response.getId()).toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OrderResponse> findById(@PathVariable int id) {
        var response = orderServiceFacade.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<OrderResponse>> getOrders(@Valid OrderFilterParams orderFilterParams,
                                                         int limit,
                                                         int page) {
        var response = orderServiceFacade.getOrders(orderFilterParams, limit, page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<OrderResponse>> findByUserId(@PathVariable int userId) {
        var response = orderServiceFacade.findByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OrderResponse> updateById(@PathVariable int id, @RequestBody UpdateOrderRequest request) {
        var response = orderServiceFacade.updateById(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        orderServiceFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatusById(@PathVariable int id, @RequestBody UpdateOrderStatusRequest request) {
        var response = orderServiceFacade.updateStatusById(id, request);
        return ResponseEntity.ok(response);
    }

}
