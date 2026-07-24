package com.coworking.space.userservice.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String number;

    private String holder;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    private boolean active;
}
