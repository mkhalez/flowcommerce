package com.coworking.space.orderservice.services;

import com.coworking.space.orderservice.dto.request.OrderFilterParams;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderStatusRequest;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    OrderResponse findById(int id);

    Page<OrderResponse> getOrders(OrderFilterParams orderFilterParams, int limit, int page);

    List<OrderResponse> findByUserId(int userId);

    OrderResponse updateById(@PathVariable int id, UpdateOrderRequest request);

    void deleteById(int id);

    OrderResponse updateStatusById(int id, UpdateOrderStatusRequest request);
}
