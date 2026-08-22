package com.coworking.space.orderservice.facades.implementation;

import com.coworking.space.orderservice.clients.UserServiceClient;
import com.coworking.space.orderservice.dto.request.OrderFilterParams;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderStatusRequest;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.dto.response.UserResponse;
import com.coworking.space.orderservice.facades.OrderServiceFacade;
import com.coworking.space.orderservice.mappers.OrderMapper;
import com.coworking.space.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderServiceFacadeImpl implements OrderServiceFacade {
    private final OrderService orderService;
    private final UserServiceClient userServiceClient;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        UserResponse user = userServiceClient.findUserByEmail(request.getEmail());
        var entity = orderService.createOrder(request, user.getId());
        return orderMapper.toOrderResponse(entity, user);
    }

    @Override
    public OrderResponse findById(int id) {
        var entity = orderService.findById(id);
        UserResponse user = userServiceClient.findById(entity.getUserId());
        return orderMapper.toOrderResponse(entity, user);
    }

    @Override
    public Page<OrderResponse> getOrders(OrderFilterParams orderFilterParams, int limit, int page) {
        UserResponse user = userServiceClient.findUserByEmail(orderFilterParams.getEmail());
        var entities = orderService.getOrders(orderFilterParams, limit, page);
        return entities.map(orderEntity -> orderMapper.toOrderResponse(orderEntity, user));
    }

    @Override
    public List<OrderResponse> findByUserId(int userId) {
        var user = userServiceClient.findById(userId);
        return orderService.findByUserId(userId)
                .stream()
                .map(orderEntity -> orderMapper.toOrderResponse(orderEntity, user))
                .toList();
    }

    @Override
    public OrderResponse updateById(int id, UpdateOrderRequest request) {
        var entity = orderService.findById(id);
        var user = userServiceClient.findById(entity.getUserId());
        entity = orderService.updateById(entity, request);
        return orderMapper.toOrderResponse(entity, user);
    }

    @Override
    public void deleteById(int id) {
        var entity = orderService.findById(id);
        userServiceClient.findById(entity.getUserId());
        orderService.deleteEntity(entity);
    }

    @Override
    public OrderResponse updateStatusById(int id, UpdateOrderStatusRequest request) {
        var entity = orderService.findById(id);
        var user = userServiceClient.findById(entity.getUserId());
        entity = orderService.updateStatusById(entity, request.getStatus());

        return orderMapper.toOrderResponse(entity, user);
    }
}
