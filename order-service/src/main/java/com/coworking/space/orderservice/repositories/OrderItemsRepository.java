package com.coworking.space.orderservice.repositories;

import com.coworking.space.orderservice.domain.entities.OrderItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Integer> {
}
