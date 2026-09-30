package com.coworking.space.authenticationservice.domain.entities;

import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "registration_event")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class RegistrationEventEntity {
    private static final int EXPONENTIAL_BASE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private RegistrationEventStatus status;

    private int attemptCount;

    private OffsetDateTime nextAttemptAt;

    private String payload;

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
