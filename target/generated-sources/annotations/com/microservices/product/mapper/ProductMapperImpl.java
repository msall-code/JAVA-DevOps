package com.microservices.product.mapper;

import com.microservices.product.dto.ProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.entity.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T17:22:30+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.description( request.getDescription() );
        product.name( request.getName() );
        product.price( request.getPrice() );
        product.stock( request.getStock() );

        return product.build();
    }

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.createdAt( product.getCreatedAt() );
        productResponse.description( product.getDescription() );
        productResponse.id( product.getId() );
        productResponse.name( product.getName() );
        productResponse.price( product.getPrice() );
        productResponse.stock( product.getStock() );
        productResponse.updatedAt( product.getUpdatedAt() );

        return productResponse.build();
    }
}
