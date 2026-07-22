package com.coworking.space.userservice.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
public class PaymentCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String number;

    private String holder;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    private boolean active;
}
