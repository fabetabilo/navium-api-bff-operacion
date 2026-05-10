package com.navium.bff_operacion.config;

import com.navium.security_lib.security.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para el BFF de Operación de Patio.
 * 
 * Esta configuración:
 * - Valida tokens JWT recibidos del frontend
 * - Restringe el acceso solo a usuarios con rol ROL_OPERADOR
 * - Configura sesiones como stateless
 * - Propaga tokens a microservicios downstream
 */
@Configuration
@EnableWebSecurity
public class BffSecurityConfig {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // desactiva CSRF por uso de JWT
            .csrf(csrf -> csrf.disable())
            
            // No guardamos sesiones, cada petición debe traer su token
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                
                // ====== RUTAS PÚBLICAS (SIN AUTENTICACIÓN) ======
                // NOTA: Si en el futuro se necesita una ruta pública (ej: health-check, Swagger),
                // agregarla aquí antes de las rutas protegidas. Ejemplo:
                // auth.requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
                
                // ====== RUTAS PROTEGIDAS (REQUIEREN TOKEN JWT Y ROL: <ROL_OPERADOR>) ======
                // Todas las rutas del BFF requieren autenticación con token JWT
                // Solo usuarios con rol ROL_OPERADOR pueden acceder a los endpoints de operación de patio
                auth.requestMatchers("/api/v0/operacion/**").hasAuthority("ROL_OPERADOR");
                
                // Por defecto, todas las demás rutas requieren autenticación
                auth.anyRequest().authenticated();
            })

            // Registra el filtro JWT antes del filtro estándar de Spring
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
