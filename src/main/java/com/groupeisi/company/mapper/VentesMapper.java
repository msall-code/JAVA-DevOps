package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.VentesDto;
import com.groupeisi.company.entities.Ventes;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VentesMapper {

    @Mapping(source = "product.ref",  target = "productRef")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "user.id",      target = "userId")
    @Mapping(source = "user.email",   target = "userEmail")
    VentesDto toDto(Ventes entity);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user",    ignore = true)
    Ventes toEntity(VentesDto dto);

    List<VentesDto> toDtoList(List<Ventes> entities);
}