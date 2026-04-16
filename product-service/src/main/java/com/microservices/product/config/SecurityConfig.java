package com.microservices.product.config;

import com.microservices.common.config.KeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String PRODUCT_API = "/api/products/**";
    
    // --- CONSTANTES POUR SONARLINT ---
    private static final String ADMIN = "ROLE_admin";
    private static final String MANAGER = "ROLE_manager";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, PRODUCT_API).permitAll() 

                // Endpoint spécifique pour le stock (accessible à tout utilisateur connecté)
                .requestMatchers(HttpMethod.PUT, "/api/products/*/stock/decrement").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/products/*/decrement").authenticated()

                // IAM & Administration utilisant les constantes
                .requestMatchers("/api/auth/users/**").hasAuthority(ADMIN)
                .requestMatchers(HttpMethod.POST, PRODUCT_API).hasAnyAuthority(ADMIN, MANAGER)
                .requestMatchers(HttpMethod.PUT, PRODUCT_API).hasAnyAuthority(ADMIN, MANAGER)
                .requestMatchers(HttpMethod.DELETE, PRODUCT_API).hasAnyAuthority(ADMIN, MANAGER)
                
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}