package com.navium.bff_operacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuración de RestClient para el BFF de Operación de Patio.
 * 
 * Esta configuración:
 * - Proporciona un RestClient.Builder con TokenPropagationInterceptor para microservicios que requieren autenticación
 * - Proporciona un RestClient.Builder sin interceptor para endpoints públicos (ej: login)
 * - Permite que los microservicios validen el token del usuario original cuando es necesario
 */
@Configuration
public class RestClientConfig {

    private final TokenPropagationInterceptor tokenPropagationInterceptor;

    public RestClientConfig(TokenPropagationInterceptor tokenPropagationInterceptor) {
        this.tokenPropagationInterceptor = tokenPropagationInterceptor;
    }

    /**
     * RestClient.Builder con TokenPropagationInterceptor.
     * 
     * Este builder se usa para comunicarse con microservicios que requieren autenticación JWT.
     * El interceptor propaga el token del BFF a los microservicios downstream.
     * 
     * @return RestClient.Builder configurado con TokenPropagationInterceptor
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(tokenPropagationInterceptor);
    }
    
    /**
     * RestClient.Builder sin TokenPropagationInterceptor.
     * 
     * Este builder se usa para comunicarse con endpoints públicos que no requieren autenticación.
     * Se utiliza principalmente para el endpoint de login del microservicio de usuarios.
     * 
     * @return RestClient.Builder configurado sin interceptores
     */
    @Bean
    public RestClient.Builder restClientBuilderWithoutAuth() {
        return RestClient.builder();
    }
}
