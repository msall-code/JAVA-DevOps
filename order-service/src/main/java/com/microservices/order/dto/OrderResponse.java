package com.microservices.order.dto;

import com.microservices.order.entity.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String customerEmail;
    private LocalDateTime createdAt;
}