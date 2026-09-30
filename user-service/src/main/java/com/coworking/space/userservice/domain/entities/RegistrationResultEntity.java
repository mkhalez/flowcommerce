package com.coworking.space.userservice.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "registration_result")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class RegistrationResultEntity {
    @Id
    private UUID id;

    @CreatedDate
    @Column(name ="created_at", nullable = false)
    private Instant createdAt;
}
