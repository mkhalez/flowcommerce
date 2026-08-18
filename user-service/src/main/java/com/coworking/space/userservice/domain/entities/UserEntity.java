package com.coworking.space.userservice.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<CardEntity> cards = new HashSet<>();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDay;

    @Column(nullable = false, unique = true)
    private String email;

    private boolean active;

    @CreatedDate
    @Column(name ="created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name ="last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "auth_user_id", nullable = false, unique = true)
    private String authUserId;
}
