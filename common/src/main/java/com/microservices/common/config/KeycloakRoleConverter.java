package com.microservices.common.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @NonNull
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        // Récupération sécurisée sans cast direct (règle le warning d'unchecked cast)
        Object realmAccessObj = jwt.getClaims().get("realm_access");

        // Utilisation du Pattern Matching de Java 16+ pour vérifier le type proprement
        if (realmAccessObj instanceof Map<?, ?> realmAccess) {
            Object rolesObj = realmAccess.get("roles");
            
            if (rolesObj instanceof List<?> rolesList) {
                return rolesList.stream()
                        .map(Object::toString)
                        .map(roleName -> "ROLE_" + roleName)
                        .map(SimpleGrantedAuthority::new)
                        // Utilisation de Collectors.toList() pour garantir une collection non nulle
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }

        // Retourne une liste vide mutable pour satisfaire @NonNull et éviter les erreurs
        return new ArrayList<>();
    }
}