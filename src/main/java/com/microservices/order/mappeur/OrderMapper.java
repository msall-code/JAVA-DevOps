package com.microservices.order.mappeur;

import com.microservices.order.dto.OrderResponse;
import com.microservices.order.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
}