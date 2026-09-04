package com.coworking.space.authenticationservice.repositories;

import com.coworking.space.authenticationservice.domain.entities.RegistrationEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface RegistrationEventRepository extends JpaRepository<RegistrationEventEntity, UUID> {

    @Query("""
        SELECT r from RegistrationEventEntity r 
        WHERE r.nextAttemptAt <= :nextAttemptAt
                AND r.status = com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus.CREATED
        order by r.createdAt
        """)
    List<RegistrationEventEntity> findEventToProcess(@Param("nextAttemptAt")OffsetDateTime nextAttemptAt, int maxAttempt, Pageable page);
}
