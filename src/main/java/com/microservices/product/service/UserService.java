// src/main/java/com/microservices/product/service/UserService.java
package com.microservices.product.service;

import com.microservices.product.dto.*;
import com.microservices.product.entity.User;
import com.microservices.product.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(req.getRole() != null ? req.getRole().toUpperCase() : "USER");
        } catch (IllegalArgumentException e) {
            role = User.Role.USER;
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(req.getPassword()) // ⚠️ pas de hash pour le TP
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        return new AuthResponse(saved.getId(), saved.getEmail(), saved.getRole().name(), "Compte créé ✓");
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        return new AuthResponse(user.getId(), user.getEmail(), user.getRole().name(), "Connexion réussie");
    }
}