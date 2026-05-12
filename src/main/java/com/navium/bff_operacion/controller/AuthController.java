package com.navium.bff_operacion.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navium.bff_operacion.client.UsuariosClient;
import com.navium.bff_operacion.web.dto.LoginRequest;
import com.navium.bff_operacion.web.dto.LoginResponse;

// separacion de responsabilidades
@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {
    
    private final UsuariosClient usuariosClient;
    
    public AuthController(UsuariosClient usuariosClient) {
        this.usuariosClient = usuariosClient;
    }
    
    /**
     * Endpoint de login del BFF.
     *
     * El BFF reenvia las credenciales al microservicio de Usuarios y devuelve
     * el JWT al frontend. El token no se almacena en el BFF; el frontend debe
     * guardarlo y enviarlo en las siguientes solicitudes.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        System.out.println("AuthController: Login endpoint llamado");
        System.out.println("AuthController: Credenciales recibidas = " + credenciales);
        
        try {
            LoginRequest request = new LoginRequest(
                credenciales.get("email"),
                credenciales.get("password")
            );
            
            LoginResponse response = usuariosClient.login(request);
            
            return ResponseEntity.ok(Map.of("token", response.token()));
            
        } catch (RuntimeException e) {
            System.out.println("AuthController: Error = " + e.getMessage());
            return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
        }
    }
}
