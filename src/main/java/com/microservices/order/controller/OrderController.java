package com.microservices.order.controller;

import com.microservices.order.dto.OrderRequest;
import com.microservices.order.dto.OrderResponse;
import com.microservices.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @NonNull @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable @NonNull Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/email/{email}")
    public List<OrderResponse> getOrdersByEmail(@PathVariable @NonNull String email) {
        return orderService.getOrdersByEmail(email);
    }

    // --- NOUVEAU : Endpoint de validation ---
    @PutMapping("/{id}/validate")
    public OrderResponse validateOrder(@PathVariable @NonNull Long id) {
        return orderService.validateOrder(id);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable @NonNull Long id) {
        return orderService.cancelOrder(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable @NonNull Long id) {
        orderService.deleteOrder(id);
    }
}