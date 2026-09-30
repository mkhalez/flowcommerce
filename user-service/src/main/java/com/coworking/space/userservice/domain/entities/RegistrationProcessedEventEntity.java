package com.coworking.space.userservice.domain.entities;

import com.coworking.space.userservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.userservice.domain.statuses.UserRegistrationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "registration_processed_event")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class RegistrationProcessedEventEntity {
    private static final int EXPONENTIAL_BASE = 2;

    @Id
    private UUID id;

    private String payload;

    @Enumerated(EnumType.STRING)
    private RegistrationEventStatus status;

    private int attemptCount;

    private OffsetDateTime nextAttemptAt;

    @CreatedDate
    @Column(name ="created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name ="last_modified_at", nullable = false)
    private Instant lastModifiedAt;

    private OffsetDateTime sendingStartedAt;

    public void incrementAttemptCount() {
        attemptCount++;
    }

    public void increaseNextAttemptAt(int base) {
        nextAttemptAt = nextAttemptAt.plusSeconds((int)(base * Math.pow(EXPONENTIAL_BASE, attemptCount)));
    }
}
