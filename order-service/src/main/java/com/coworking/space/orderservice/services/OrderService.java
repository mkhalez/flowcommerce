package com.coworking.space.orderservice.services;

import com.coworking.space.orderservice.domain.entities.OrderEntity;
import com.coworking.space.orderservice.domain.enums.Status;
import com.coworking.space.orderservice.dto.request.OrderFilterParams;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderStatusRequest;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface OrderService {
    OrderEntity createOrder(OrderRequest request, int userId);

    OrderEntity findById(int id);

    Page<OrderEntity> getOrders(OrderFilterParams orderFilterParams, int limit, int page);

    List<OrderEntity> findByUserId(int userId);

    OrderEntity updateById(@PathVariable int id, UpdateOrderRequest request);

    void deleteEntity(OrderEntity entity);

    OrderEntity updateStatusById(int id, UpdateOrderStatusRequest request);
}
