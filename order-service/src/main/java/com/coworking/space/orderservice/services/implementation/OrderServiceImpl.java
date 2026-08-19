package com.coworking.space.orderservice.services.implementation;

import com.coworking.space.orderservice.clients.UserServiceClient;
import com.coworking.space.orderservice.domain.entities.ItemEntity;
import com.coworking.space.orderservice.domain.entities.OrderEntity;
import com.coworking.space.orderservice.domain.entities.OrderItemsEntity;
import com.coworking.space.orderservice.domain.enums.Status;
import com.coworking.space.orderservice.domain.exceptions.ItemNotFoundException;
import com.coworking.space.orderservice.dto.request.OrderFilterParams;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.dto.response.UserResponse;
import com.coworking.space.orderservice.repositories.ItemRepository;
import com.coworking.space.orderservice.repositories.OrderItemsRepository;
import com.coworking.space.orderservice.repositories.OrderRepository;
import com.coworking.space.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserServiceClient userServiceClient;
    private final ItemRepository itemRepo;
    private final OrderRepository orderRepo;
    private final OrderItemsRepository orderItemsRepo;

    private static final String NOT_FOUND_ITEMS_ERROR = "products with these ids were not found: ";
    private static final boolean NOT_DELETED_STATE = false;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        UserResponse user = userServiceClient.findUserByEmail(request.getEmail());
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(user.getUserId());

        var ids = request.getItems().stream()
                .map(OrderRequest.OrderItemRequest::getItemId)
                .toList();

        List<ItemEntity> itemEntities = itemRepo.findAllById(ids);

        Map<Integer, ItemEntity> itemMap = itemEntities.stream()
                .collect(Collectors.toMap(ItemEntity::getId, item -> item));

        var missingIds = ids.stream()
                .filter(id -> !itemMap.containsKey(id))
                .toList();

        if(!missingIds.isEmpty()) {
            throw new ItemNotFoundException(NOT_FOUND_ITEMS_ERROR + missingIds);
        }

        Set<OrderItemsEntity> orderItemsEntities = new HashSet<>();
        double totalPrice = 0;
        for(var itemRequest : request.getItems()) {
            ItemEntity itemEntity = itemMap.get(itemRequest.getItemId());

            OrderItemsEntity orderItemsEntity = OrderItemsEntity.builder()
                    .order(orderEntity)
                    .item(itemEntity)
                    .quantity(itemRequest.getQuantity())
                    .deleted(NOT_DELETED_STATE)
                    .build();

            orderItemsEntities.add(orderItemsEntity);
            totalPrice += orderItemsEntity.getQuantity() * itemEntity.getPrice();
        }

        orderEntity.setDeleted(NOT_DELETED_STATE);
        orderEntity.setStatus(Status.CREATED);
        orderEntity.setTotalPrice(totalPrice);
        orderEntity.setOrderItemsEntities(orderItemsEntities);

        orderRepo.save(orderEntity);

    }

    @Override
    public OrderResponse findById(int id) {
        return null;
    }

    @Override
    public Page<OrderResponse> getOrders(OrderFilterParams orderFilterParams, int limit, int page) {
        return null;
    }

    @Override
    public OrderResponse findByUserId(int userId) {
        return null;
    }

    @Override
    public OrderResponse updateById(int id, OrderRequest request) {
        return null;
    }

    @Override
    public void deleteById(int id) {

    }
}
