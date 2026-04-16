package com.microservices.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_orders")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    // Utilise l'Enum OrderStatus
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String customerEmail;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        // Définit le statut par défaut à la création
        if (this.status == null) this.status = OrderStatus.CREATED;
    }
}