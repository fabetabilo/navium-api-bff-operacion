package com.navium.bff_operacion.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.navium.bff_operacion.web.dto.LoginRequest;
import com.navium.bff_operacion.web.dto.LoginResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 * Client para comunicarse con el microservicio de usuarios.
 * 
 * Este client:
 * - Usa RestClient.Builder sin TokenPropagationInterceptor para el endpoint de login (público)
 * - Proporciona fallback para manejar fallos del microservicio de usuarios
 * - Implementa circuit breaker para evitar cascadas de fallos
 */
@Component
public class UsuariosClient {
    
    private final RestClient restClient;
    
    /**
     * Constructor que inyecta el RestClient.Builder sin autenticación.
     * 
     * @param restClientBuilderWithoutAuth RestClient.Builder configurado sin TokenPropagationInterceptor
     * @param msUrl URL del microservicio de usuarios
     */
    public UsuariosClient(
            @Qualifier("restClientBuilderWithoutAuth") RestClient.Builder restClientBuilder, 
            @Value("${navium.ms.usuarios.url}") String msUrl) {
        this.restClient = restClientBuilder.baseUrl(msUrl).build();
    }
    
    /**
     * Realiza el login de usuario en el microservicio de usuarios.
     * 
     * Este endpoint es público y no requiere autenticación JWT.
     * El microservicio de usuarios valida las credenciales y devuelve un token JWT.
     * 
     * @param request Credenciales de login (email y password)
     * @return LoginResponse con el token JWT
     * @throws RuntimeException si el microservicio no está disponible (circuit breaker activado)
     */
    @CircuitBreaker(name = "usuariosCb", fallbackMethod = "loginFallback")
    public LoginResponse login(LoginRequest request) {
        try {
            LoginResponse response = restClient.post()
                    .uri("/api/auth/login")
                    .body(request)
                    .retrieve()
                    .body(LoginResponse.class);
            
            //System.out.println("UsuariosClient: Login exitoso, token recibido");
            return response;
        } catch (Exception e) {
            //System.out.println("UsuariosClient: Error en login - " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // --- FALLBACKS ---

    /**
     * Fallback para el endpoint de login cuando el microservicio no está disponible.
     * 
     * @param request Credenciales de login (no usado en el fallback)
     * @param t Throwable que causó el fallo
     * @return LoginResponse (nunca se retorna, siempre lanza excepción)
     * @throws RuntimeException con mensaje de error descriptivo
     */
    private LoginResponse loginFallback(LoginRequest request, Throwable t) {
        throw new RuntimeException("El servicio de Usuarios no está disponible para realizar el login en este momento.");
    }
}
