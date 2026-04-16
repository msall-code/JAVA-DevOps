package com.microservices.product.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {
    private final Integer currentStock;
    private final Integer requestedQuantity;

    public InsufficientStockException(Long id, Integer currentStock, Integer requestedQuantity) {
        super(String.format("Stock insuffisant pour le produit %d (Disponible: %d, Demandé: %d)",
                id, currentStock, requestedQuantity));
        this.currentStock = currentStock;
        this.requestedQuantity = requestedQuantity;
    }
}