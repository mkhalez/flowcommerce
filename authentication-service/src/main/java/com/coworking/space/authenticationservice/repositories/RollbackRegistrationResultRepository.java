package com.coworking.space.authenticationservice.repositories;

import com.coworking.space.authenticationservice.domain.entities.RollbackRegistrationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RollbackRegistrationResultRepository extends JpaRepository<RollbackRegistrationResultEntity, UUID> {
}
