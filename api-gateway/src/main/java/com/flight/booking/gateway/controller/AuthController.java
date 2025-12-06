package com.flight.booking.gateway.controller;

import com.flight.booking.gateway.dto.AuthRequest;
import com.flight.booking.gateway.dto.AuthResponse;
import com.flight.booking.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        // TODO: In production, validate credentials against user database
        // For now, accepting any username/password for demo purposes

        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        String token = jwtUtil.generateToken(request.getUsername());
        AuthResponse response = new AuthResponse(token, request.getUsername());

        return Mono.just(ResponseEntity.ok(response));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody AuthRequest request) {
        // TODO: In production, save user to database
        // For now, just generating token

        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        String token = jwtUtil.generateToken(request.getUsername());
        AuthResponse response = new AuthResponse(token, request.getUsername());

        return Mono.just(ResponseEntity.ok(response));
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<String>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            return Mono.just(ResponseEntity.ok("Token is valid for user: " + username));
        } else {
            return Mono.just(ResponseEntity.status(401).body("Invalid or expired token"));
        }
    }
}