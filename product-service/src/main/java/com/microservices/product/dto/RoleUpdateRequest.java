package com.microservices.product.dto;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    private String userId;   // L'ID unique de l'utilisateur dans Keycloak
    private String roleName; // Ex: "MANAGER" ou "ADMIN"
}