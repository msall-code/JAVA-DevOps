package com.microservices.order.client;

import com.microservices.order.dto.ProductResponse;
import com.microservices.order.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}/stock/decrement")
    void decrementStock(@PathVariable("id") Long id, @RequestBody StockUpdateRequest request);
}