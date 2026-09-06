package com.coworking.space.userservice.repositories;

import com.coworking.space.userservice.domain.entities.RegistrationProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationProcessedEventRepository extends JpaRepository<RegistrationProcessedEventEntity, UUID> {
    @Query(value = """
        SELECT * from registration_processed_event
        WHERE attempt_count <= :maxAttempt
                AND (
                    (status = 'CREATED' AND next_attempt_at < :now)
                    OR (status = 'PRE_SENDING' AND sending_started_at <= :sendingTimeoutThreshold))
        ORDER BY created_at
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<RegistrationProcessedEventEntity> findEventToProcess(
            @Param("now") OffsetDateTime now,
            @Param("sendingTimeoutThreshold") OffsetDateTime sendingTimeoutThreshold,
            @Param("maxAttempt") int maxAttempt,
            @Param("limit") int limit);
}
