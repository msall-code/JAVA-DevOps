package com.microservices.product.controller;

import com.microservices.product.dto.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        try (Keycloak keycloak = getAdminKeycloak()) {
            List<UserRepresentation> users = keycloak.realm(realm).users().list();

            List<Map<String, Object>> enrichedUsers = users.stream().map(user -> {
                List<String> roles = keycloak.realm(realm).users().get(user.getId())
                        .roles().realmLevel().listAll().stream()
                        .map(RoleRepresentation::getName)
                        .filter(name -> !name.startsWith("default-roles"))
                        .collect(Collectors.toList());

                Map<String, Object> userMap = new java.util.HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("email", user.getEmail() != null ? user.getEmail() : "N/A");
                userMap.put("enabled", user.isEnabled());
                userMap.put("role", roles.contains("Admin") ? "Admin" : (roles.contains("manager") ? "manager" : "user"));
                
                return userMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(enrichedUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur Keycloak : " + e.getMessage());
        }
    }

    @PutMapping("/users/{id}/assign-manager")
    public ResponseEntity<?> assignManagerRole(@PathVariable String id) {
        try (Keycloak keycloak = getAdminKeycloak()) {
            RoleRepresentation managerRole = keycloak.realm(realm).roles().get("manager").toRepresentation();
            keycloak.realm(realm).users().get(id).roles().realmLevel().add(Collections.singletonList(managerRole));
            return ResponseEntity.ok(Map.of("message", "Rôle Manager assigné avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }

    // --- NOUVELLE MÉTHODE : DÉNOMMER MANAGER ---
    @PutMapping("/users/{id}/demote-manager")
    public ResponseEntity<?> demoteManagerRole(@PathVariable String id) {
        try (Keycloak keycloak = getAdminKeycloak()) {
            RoleRepresentation managerRole = keycloak.realm(realm).roles().get("manager").toRepresentation();
            keycloak.realm(realm).users().get(id).roles().realmLevel().remove(Collections.singletonList(managerRole));
            return ResponseEntity.ok(Map.of("message", "Rôle Manager révoqué avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try (Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(loginData.get("username"))
                .password(loginData.get("password"))
                .grantType(OAuth2Constants.PASSWORD)
                .build()) {

            var tokenResponse = keycloak.tokenManager().getAccessToken();
            return ResponseEntity.ok(Map.of(
                    "token", tokenResponse.getToken(),
                    "username", loginData.get("username")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto userDto) {
        UserRepresentation user = mapToUserRepresentation(userDto);
        try (Keycloak keycloak = getAdminKeycloak()) {
            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() == 201) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur créé !");
                } else {
                    return ResponseEntity.status(response.getStatus()).body("Erreur création utilisateur");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }

    @PutMapping("/users/{id}/toggle-role")
    public ResponseEntity<?> toggleUserStatus(@PathVariable String id) {
        try (Keycloak keycloak = getAdminKeycloak()) {
            UserRepresentation user = keycloak.realm(realm).users().get(id).toRepresentation();
            user.setEnabled(!user.isEnabled());
            keycloak.realm(realm).users().get(id).update(user);
            return ResponseEntity.ok(Map.of("message", "Statut mis à jour", "status", user.isEnabled()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }

    private UserRepresentation mapToUserRepresentation(UserRegistrationDto userDto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(userDto.getPassword());
        user.setCredentials(Collections.singletonList(cred));
        return user;
    }

    private Keycloak getAdminKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}