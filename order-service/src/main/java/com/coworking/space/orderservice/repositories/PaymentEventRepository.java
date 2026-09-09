package com.coworking.space.orderservice.repositories;

import com.coworking.space.orderservice.domain.entities.PaymentEventEntity;
import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentEventRepository extends JpaRepository<PaymentEventEntity, String> {
}
