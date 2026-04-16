package com.microservices.product.mapper;

import com.microservices.product.dto.ProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Pour pouvoir l'injecter avec @Autowired
public interface ProductMapper {

    // Transforme la requête du front en Entité pour la DB
    @Mapping(target = "id", ignore = true) // L'ID est généré par la DB
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequest request);

    // Transforme l'Entité de la DB en Réponse pour le front
    ProductResponse toResponse(Product product);
}