package com.coworking.space.orderservice.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;

@Entity
@Table(name = "order_items")
@SoftDelete
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class OrderItemsEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    private Integer quantity;
}
