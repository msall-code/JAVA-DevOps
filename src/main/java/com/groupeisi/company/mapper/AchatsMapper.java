package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.AchatsDto;
import com.groupeisi.company.entities.Achats;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AchatsMapper {

    @Mapping(source = "product.ref",  target = "productRef")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "user.id",      target = "userId")
    @Mapping(source = "user.email",   target = "userEmail")
    @Mapping(source = "venteValidee", target = "venteValidee")
    AchatsDto toDto(Achats entity);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user",    ignore = true)
    Achats toEntity(AchatsDto dto);

    List<AchatsDto> toDtoList(List<Achats> entities);
}