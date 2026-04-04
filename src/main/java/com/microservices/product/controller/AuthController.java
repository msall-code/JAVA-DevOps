package com.microservices.product.controller;


import com.microservices.product.dto.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

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

    // --- 1. MÉTHODE OUTIL : Prépare l'objet utilisateur pour Keycloak ---
    private UserRepresentation mapToUserRepresentation(UserRegistrationDto userDto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getUsername());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(userDto.getPassword());

        user.setCredentials(Collections.singletonList(cred));
        return user;
    }

    // --- 2. MÉTHODE OUTIL : Obtient la connexion Admin (Service Account) ---
    private Keycloak getAdminKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    // --- 3. ENDPOINT LOGIN ---
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

            // On crée une réponse personnalisée pour le Front
            return ResponseEntity.ok(Map.of(
                    "token", tokenResponse.getToken(),
                    "role", "ADMIN", // On force ADMIN pour tes tests msall
                    "username", loginData.get("username")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects");
        }
    }

    // --- 4. ENDPOINT REGISTER (Nettoyé) ---
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto userDto) {
        // On utilise la méthode outil (1) pour créer l'objet
        UserRepresentation user = mapToUserRepresentation(userDto);

        // On utilise la méthode outil (2) pour la connexion
        try (Keycloak keycloak = getAdminKeycloak()) {
            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() == 201) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur créé avec succès !");
                } else if (response.getStatus() == 409) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("L'utilisateur existe déjà.");
                } else {
                    return ResponseEntity.status(response.getStatus()).body("Erreur Keycloak");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }
}