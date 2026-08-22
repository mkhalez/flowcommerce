package com.coworking.space.orderservice.services.implementation;

import com.coworking.space.orderservice.domain.entities.ItemEntity;
import com.coworking.space.orderservice.domain.entities.OrderEntity;
import com.coworking.space.orderservice.domain.entities.OrderItemsEntity;
import com.coworking.space.orderservice.domain.enums.Status;
import com.coworking.space.orderservice.domain.exceptions.ItemNotFoundException;
import com.coworking.space.orderservice.domain.exceptions.OrderNotFoundException;
import com.coworking.space.orderservice.domain.exceptions.OrderStatusIsNotCreated;
import com.coworking.space.orderservice.dto.request.*;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.dto.response.UserResponse;
import com.coworking.space.orderservice.repositories.ItemRepository;
import com.coworking.space.orderservice.repositories.OrderRepository;
import com.coworking.space.orderservice.repositories.specification.OrderSpecification;
import com.coworking.space.orderservice.services.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ItemRepository itemRepo;
    private final OrderRepository orderRepo;
    private final OrderSpecification orderSpecification;

    private static final String NOT_FOUND_ITEMS_ERROR = "products with these ids were not found: ";
    private static final String NOT_FOUND_ORDER_ERROR = "not found order";
    private static final String SORTING_BY_ID = "id";
    private static final String ORDER_STATUS_IS_NOT_CREATED_ERROR = "order status is not created";

    @Override
    @Transactional
    public OrderEntity createOrder(OrderRequest request, int userId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);

        var orderItemsEntitiesAndTotalPrice = getOrderItemsEntities(request.getItems(), orderEntity);

        orderEntity.setStatus(Status.CREATED);
        orderEntity.setTotalPrice(orderItemsEntitiesAndTotalPrice.getSecond());
        orderEntity.setOrderItemsEntities(orderItemsEntitiesAndTotalPrice.getFirst());

        return orderRepo.save(orderEntity);
    }

    @Override
    public OrderEntity findById(int id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_ORDER_ERROR));
    }

    @Override
    public Page<OrderEntity> getOrders(OrderFilterParams orderFilterParams, int limit, int pageNum) {
        Pageable page = PageRequest.of(pageNum, limit, Sort.by(SORTING_BY_ID));
        Specification<OrderEntity> spec = Specification.<OrderEntity>unrestricted()
                .and(orderSpecification.createFrom(orderFilterParams.getFrom()))
                .and(orderSpecification.createTo(orderFilterParams.getTo()))
                .and(orderSpecification.hasStatus(orderFilterParams.getStatus()));

        return orderRepo.findAll(spec, page);
    }

    @Override
    public List<OrderEntity> findByUserId(int userId) {
        return orderRepo.findByUserId(userId);
    }

    @Override
    @Transactional
    public OrderEntity updateById(int id, UpdateOrderRequest request) {
        var orderEntity = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_ORDER_ERROR));

        if(orderEntity.getStatus() != Status.CREATED) {
            throw new OrderStatusIsNotCreated(ORDER_STATUS_IS_NOT_CREATED_ERROR);
        }

        var orderItemsEntitiesAndTotalPrice = getOrderItemsEntities(request.getItems(), orderEntity);
        orderEntity.getOrderItemsEntities().clear();
        orderEntity.getOrderItemsEntities().addAll(orderItemsEntitiesAndTotalPrice.getFirst());
        orderEntity.setTotalPrice(orderItemsEntitiesAndTotalPrice.getSecond());

        return orderRepo.save(orderEntity);
    }

    @Override
    @Transactional
    public void deleteEntity(OrderEntity entity) {
        orderRepo.delete(entity);
    }

    @Override
    @Transactional
    public OrderEntity updateStatusById(int id, UpdateOrderStatusRequest request) {
        var entity = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_ORDER_ERROR));
        entity.setStatus(request.getStatus());
        return orderRepo.save(entity);
    }

    private Pair<Set<OrderItemsEntity>, Double> getOrderItemsEntities(List<OrderItemRequest> orderItemRequests, OrderEntity orderEntity) {
        var ids = orderItemRequests.stream()
                .map(OrderItemRequest::getItemId)
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
        for(var itemRequest : orderItemRequests) {
            ItemEntity itemEntity = itemMap.get(itemRequest.getItemId());

            OrderItemsEntity orderItemsEntity = OrderItemsEntity.builder()
                    .order(orderEntity)
                    .item(itemEntity)
                    .quantity(itemRequest.getQuantity())
                    .build();

            orderItemsEntities.add(orderItemsEntity);
            totalPrice += orderItemsEntity.getQuantity() * itemEntity.getPrice();
        }

        return Pair.of(orderItemsEntities, totalPrice);
    }

}
