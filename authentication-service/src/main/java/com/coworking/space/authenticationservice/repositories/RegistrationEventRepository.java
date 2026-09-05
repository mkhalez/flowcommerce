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

    @Query(value = """
        SELECT * from registration_event
        WHERE attempt_count <= :maxAttempt
                AND (
                    (status = 'CREATED' AND next_attempt_at < :now)
                    OR (status = 'PRE_SENDING' AND sending_started_at <= :sendingTimeoutThreshold))
        ORDER BY created_at
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<RegistrationEventEntity> findEventToProcess(
            @Param("now") OffsetDateTime now,
            @Param("sendingTimeoutThreshold") OffsetDateTime sendingTimeoutThreshold,
            @Param("maxAttempt") int maxAttempt,
            @Param("limit") int limit);
}
