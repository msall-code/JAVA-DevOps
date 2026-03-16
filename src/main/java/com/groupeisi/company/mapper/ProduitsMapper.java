package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.ProduitsDto;
import com.groupeisi.company.entities.Produits;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProduitsMapper {

    @Mapping(source = "user.id",    target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    ProduitsDto toDto(Produits entity);

    @Mapping(target = "user", ignore = true)
    Produits toEntity(ProduitsDto dto);

    List<ProduitsDto> toDtoList(List<Produits> entities);
}