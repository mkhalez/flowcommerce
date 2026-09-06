package com.coworking.space.userservice.repositories;

import com.coworking.space.userservice.domain.entities.RegistrationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistrationResultRepository extends JpaRepository<RegistrationResultEntity, UUID> {
}
