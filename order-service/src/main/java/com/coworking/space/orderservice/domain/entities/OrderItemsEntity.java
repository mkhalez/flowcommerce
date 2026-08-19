package com.coworking.space.orderservice.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.SoftDelete;

@Entity
@Table(name = "order_items")
@SoftDelete
public class OrderItemsEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    private Integer quantity;

    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;
}
