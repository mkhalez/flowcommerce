package com.coworking.space.orderservice.domain.entities;

import com.coworking.space.orderservice.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@SoftDelete
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class OrderEntity extends BaseEntity{
    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private Boolean deleted;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemsEntity> orderItemsEntities = new HashSet<>();
}
