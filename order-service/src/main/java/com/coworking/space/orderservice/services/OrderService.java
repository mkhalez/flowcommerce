package com.coworking.space.orderservice.services;

import com.coworking.space.orderservice.dto.request.OrderFilterParams;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    OrderResponse findById(int id);

    Page<OrderResponse> getOrders(OrderFilterParams orderFilterParams, int limit, int page);

    OrderResponse findByUserId(int userId);

    OrderResponse updateById(@PathVariable int id, OrderRequest request);

    void deleteById(int id);
}
