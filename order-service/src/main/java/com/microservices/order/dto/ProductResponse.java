package com.microservices.order.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price; // BigDecimal pour la précision financière
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}