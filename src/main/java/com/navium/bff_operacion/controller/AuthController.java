package com.navium.bff_operacion.controller;

import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
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
        try {
            LoginRequest request = new LoginRequest(
                credenciales.get("email"),
                credenciales.get("password")
            );
            
            LoginResponse response = usuariosClient.login(request);
            
            return ResponseEntity.ok(Map.of("token", response.token()));
            
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String message = e.getResponseBodyAsString();
            if (message == null || message.isBlank()) {
                message = "Credenciales invalidas";
            }
            return ResponseEntity.status(status).body(Map.of("error", message));
        } catch (HttpServerErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String message = e.getResponseBodyAsString();
            if (message == null || message.isBlank()) {
                message = "Error en el servicio de Usuarios";
            }
            return ResponseEntity.status(status).body(Map.of("error", message));
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(503).body(Map.of("error", "El servicio de Usuarios no esta disponible"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error inesperado en el login"));
        }
    }
}
