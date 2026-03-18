package com.microservices.product.dto;

// src/main/java/com/microservices/product/dto/AuthResponse.java

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String email;
    private String role;
    private String message;
}