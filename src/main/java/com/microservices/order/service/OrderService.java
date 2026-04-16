package com.microservices.order.service;

import com.microservices.order.client.ProductClient;
import com.microservices.order.dto.*;
import com.microservices.order.entity.Order;
import com.microservices.order.entity.OrderStatus;
import com.microservices.order.exception.OrderNotFoundException;
import com.microservices.order.mappeur.OrderMapper;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;

    public OrderResponse createOrder(@NonNull OrderRequest request) {
        Long productId = request.getProductId();
        if (productId == null) throw new IllegalArgumentException("ID produit manquant");

        ProductResponse product = productClient.getProductById(productId);
        
        if (product == null) {
            throw new OrderNotFoundException("Produit non trouvé avec l'ID: " + productId);
        }
        
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalStateException("Stock insuffisant pour le produit: " + product.getName());
        }

        productClient.decrementStock(productId, new StockUpdateRequest(request.getQuantity()));

        Order order = Order.builder()
                .productId(productId)
                .productName(product.getName())
                .quantity(request.getQuantity())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .customerEmail(request.getCustomerEmail())
                .status(OrderStatus.CREATED)
                .build();

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    // --- NOUVEAU : Méthode de validation pour l'Admin ---
    // Dans OrderService.java
@Transactional
public OrderResponse validateOrder(@NonNull Long id) {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée ID: " + id));
    
    // On passe directement de CREATED à VALIDATED
    order.setStatus(OrderStatus.VALIDATED);
    
    return orderMapper.toResponse(orderRepository.save(order));
}

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(@NonNull Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByEmail(@NonNull String email) {
        return orderRepository.findByCustomerEmail(email).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse cancelOrder(@NonNull Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Impossible d'annuler. Commande non trouvée avec l'ID: " + id));
        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    public void deleteOrder(@NonNull Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Impossible de supprimer. Commande non trouvée avec l'ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}