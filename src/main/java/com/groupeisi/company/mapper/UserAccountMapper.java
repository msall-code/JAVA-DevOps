package com.groupeisi.company.mapper;

import com.groupeisi.company.dto.UserAccountDto;
import com.groupeisi.company.entities.UserAccount;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    @Mapping(target = "role",
            expression = "java(entity.getRole() != null ? entity.getRole().name() : null)")
    UserAccountDto toDto(UserAccount entity);

    @Mapping(target = "role",
            expression = "java(dto.getRole() != null ? UserAccount.Role.valueOf(dto.getRole()) : UserAccount.Role.USER)")
    UserAccount toEntity(UserAccountDto dto);

    List<UserAccountDto> toDtoList(List<UserAccount> entities);
}