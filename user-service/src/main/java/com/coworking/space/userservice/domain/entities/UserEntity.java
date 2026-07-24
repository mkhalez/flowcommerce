package com.coworking.space.userservice.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CardEntity> cards;

    private String name;

    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDay;

    private String email;

    private boolean active;
}
