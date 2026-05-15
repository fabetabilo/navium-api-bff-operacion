package com.navium.bff_operacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Configuración de seguridad personalizada para el BFF de Operación de Patio.
 * 
 * Esta configuración extiende el SecurityConfig de navium-security-lib mediante
 * un Customizer que agrega reglas de autorización específicas para este BFF.
 * 
 * Reglas agregadas:
 * - /api/v0/auth/login: pública (sin autenticación) para login de usuarios
 * - /api/v0/operacion/**: requiere rol ROL_OPERADOR
 */
@Configuration
public class BffSecurityConfig {

    /**
     * Customizer que agrega reglas de autorización específicas del BFF.
     * 
     * Este customizer es inyectado en el SecurityConfig de navium-security-lib
     * y agrega las reglas específicas del BFF de operación de patio.
     * 
     * @return Customizer con las reglas de autorización del BFF
     */
    @Bean
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeCustomizer() {
        return auth -> {
            // ====== RUTAS PÚBLICAS (SIN AUTENTICACIÓN) ======
            auth.requestMatchers("/api/v0/auth/login").permitAll();
            
            // ====== RUTAS PROTEGIDAS (REQUIEREN TOKEN JWT Y ROL: <ROL_OPERADOR>) ======
            auth.requestMatchers("/api/v0/operacion/**").hasAuthority("ROL_OPERADOR");
        };
    }
}
