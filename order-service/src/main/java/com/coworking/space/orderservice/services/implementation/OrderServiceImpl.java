package com.coworking.space.orderservice.services.implementation;

import com.coworking.space.orderservice.clients.UserServiceClient;
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
import com.coworking.space.orderservice.mappers.OrderMapper;
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
    private final UserServiceClient userServiceClient;
    private final ItemRepository itemRepo;
    private final OrderRepository orderRepo;
    private final OrderSpecification orderSpecification;
    private final OrderMapper orderMapper;

    private static final String NOT_FOUND_ITEMS_ERROR = "products with these ids were not found: ";
    private static final String NOT_FOUND_ORDER_ERROR = "not found order";
    private static final String SORTING_BY_ID = "id";
    private static final String ORDER_STATUS_IS_NOT_CREATED_ERROR = "order status is not created";

    @Override
    @Transactional
    @CircuitBreaker(name = "UserServiceCB")
    public OrderResponse createOrder(OrderRequest request) {
        UserResponse user = userServiceClient.findUserByEmail(request.getEmail());
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(user.getId());

        var orderItemsEntitiesAndTotalPrice = getOrderItemsEntities(request.getItems(), orderEntity);

        orderEntity.setStatus(Status.CREATED);
        orderEntity.setTotalPrice(orderItemsEntitiesAndTotalPrice.getSecond());
        orderEntity.setOrderItemsEntities(orderItemsEntitiesAndTotalPrice.getFirst());

        var entity = orderRepo.save(orderEntity);
        return orderMapper.toOrderResponse(entity, user);
    }

    @Override
    @CircuitBreaker(name = "UserServiceCB")
    public OrderResponse findById(int id) {
        var entity = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_ORDER_ERROR));

        UserResponse user = userServiceClient.findById(entity.getUserId());

        return orderMapper.toOrderResponse(entity, user);
    }

    @Override
    @CircuitBreaker(name = "UserServiceCB")
    public Page<OrderResponse> getOrders(OrderFilterParams orderFilterParams, int limit, int pageNum) {
        Pageable page = PageRequest.of(pageNum, limit, Sort.by(SORTING_BY_ID));
        Specification<OrderEntity> spec = Specification.<OrderEntity>unrestricted()
                .and(orderSpecification.createFrom(orderFilterParams.getFrom()))
                .and(orderSpecification.createTo(orderFilterParams.getTo()))
                .and(orderSpecification.hasStatus(orderFilterParams.getStatus()));

        var entities = orderRepo.findAll(spec, page);

        List<Integer> userIds = entities.getContent().stream()
                .map(OrderEntity::getUserId)
                .distinct()
                .toList();

        UserResponse user = userServiceClient.findUserByEmail(orderFilterParams.getEmail());

        return entities.map(orderEntity -> orderMapper.toOrderResponse(orderEntity, user));
    }

    @Override
    @CircuitBreaker(name = "UserServiceCB")
    public List<OrderResponse> findByUserId(int userId) {
        var orderEntities = orderRepo.findByUserId(userId);
        var user = userServiceClient.findById(userId);

        return orderEntities
                .stream()
                .map(orderEntity -> orderMapper.toOrderResponse(orderEntity, user))
                .toList();
    }

    @Override
    @CircuitBreaker(name = "UserServiceCB")
    @Transactional
    public OrderResponse updateById(int id, UpdateOrderRequest request) {
        var orderEntity = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_ORDER_ERROR));

        if(orderEntity.getStatus() != Status.CREATED) {
            throw new OrderStatusIsNotCreated(ORDER_STATUS_IS_NOT_CREATED_ERROR);
        }

        var user = userServiceClient.findById(orderEntity.getUserId());

        var orderItemsEntitiesAndTotalPrice = getOrderItemsEntities(request.getItems(), orderEntity);
        orderEntity.getOrderItemsEntities().clear();
        orderEntity.getOrderItemsEntities().addAll(orderItemsEntitiesAndTotalPrice.getFirst());
        orderEntity.setTotalPrice(orderItemsEntitiesAndTotalPrice.getSecond());

        var newEntity = orderRepo.save(orderEntity);
        return orderMapper.toOrderResponse(newEntity, user);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        var entity = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_ORDER_ERROR));
        userServiceClient.findById(entity.getUserId());

        orderRepo.delete(entity);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "UserServiceCB")
    public OrderResponse updateStatusById(int id, UpdateOrderStatusRequest request) {
        var entity = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_ORDER_ERROR));
        var user = userServiceClient.findById(entity.getUserId());
        entity.setStatus(request.getStatus());

        var newEntity = orderRepo.save(entity);
        return orderMapper.toOrderResponse(newEntity, user);
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
