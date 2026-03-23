// src/main/java/com/microservices/product/controller/UserController.java
package com.microservices.product.controller;

import com.microservices.product.dto.*;
import com.microservices.product.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5173"
})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(userService.login(req));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, e.getMessage()));
        }
    }
}