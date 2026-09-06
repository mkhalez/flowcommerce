package com.coworking.space.userservice.repositories;

import com.coworking.space.userservice.domain.entities.RegistrationProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RegistrationProcessedEventRepository extends JpaRepository<RegistrationProcessedEventEntity, UUID> {
}
