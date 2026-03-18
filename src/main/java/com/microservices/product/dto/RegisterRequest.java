package com.microservices.product.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String role; // "ADMIN", "MANAGER", "USER"
}