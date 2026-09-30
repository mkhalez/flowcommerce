package com.coworking.space.authenticationservice.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rollback_result")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class RollbackRegistrationResultEntity {
    @Id
    private UUID id;

    @CreatedDate
    @Column(name ="created_at", nullable = false)
    private Instant createdAt;

    private String message;
}
