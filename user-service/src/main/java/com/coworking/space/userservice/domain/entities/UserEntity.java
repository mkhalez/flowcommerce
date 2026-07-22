package com.coworking.space.userservice.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PaymentCardEntity> cards;

    private String name;

    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDay;

    private String email;

    private boolean active;
}
