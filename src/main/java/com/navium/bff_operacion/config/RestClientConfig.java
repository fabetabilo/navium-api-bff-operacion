package com.navium.bff_operacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private final TokenPropagationInterceptor tokenPropagationInterceptor;

    public RestClientConfig(TokenPropagationInterceptor tokenPropagationInterceptor) {
        this.tokenPropagationInterceptor = tokenPropagationInterceptor;
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(tokenPropagationInterceptor);
    }
}
