package com.coworking.space.orderservice.repositories;

import com.coworking.space.orderservice.domain.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {
}
