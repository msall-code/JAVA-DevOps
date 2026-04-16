package com.microservices.order.mappeur;

import com.microservices.order.dto.OrderResponse;
import com.microservices.order.entity.Order;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T16:50:39+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.createdAt( order.getCreatedAt() );
        orderResponse.customerEmail( order.getCustomerEmail() );
        orderResponse.id( order.getId() );
        orderResponse.productId( order.getProductId() );
        orderResponse.productName( order.getProductName() );
        orderResponse.quantity( order.getQuantity() );
        orderResponse.status( order.getStatus() );
        orderResponse.totalPrice( order.getTotalPrice() );

        return orderResponse.build();
    }
}
