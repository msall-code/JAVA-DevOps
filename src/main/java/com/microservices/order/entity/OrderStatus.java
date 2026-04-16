package com.microservices.order.entity;

public enum OrderStatus {
    CREATED,    // En attente (par défaut)
    VALIDATED,  // Validée par l'Admin
    CANCELLED,  // Annulée
   // DELIVERED   // Livrée
}