package com.coworking.space.orderservice.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "payment_event")
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PaymentEventEntity {
    @Id
    private String id;

    @CreatedDate
    @Column(nullable = false)
    private Instant createdAt;
}
