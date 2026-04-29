package com.navium.bff_operacion.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.navium.bff_operacion.client.dto.ContenedorResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class ContenedoresClient {
    
    private final RestClient restClient;
    
    public ContenedoresClient(RestClient.Builder restClientBuilder, @Value("${navium.ms.contenedores.url}") String msUrl) {
        this.restClient = restClientBuilder.baseUrl(msUrl).build();
    }
    
    @CircuitBreaker(name = "contenedoresCb", fallbackMethod = "obtenerContenedoresPatioFallback")
    public List<ContenedorResponse> obtenerContenedoresPatio() {
        return restClient.get()
                .uri("/api/contenedores/patio")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ContenedorResponse>>() {});
    }
    
    /** IMPORTANTE!!: REVISAR -- --- --- --- --- --- REVISAR!!! */
    public ContenedorResponse actualizarAnden(Long idContenedor, String ubicacionAnden) {
        String uri = UriComponentsBuilder.fromPath("/api/contenedores/{id}/anden")
                .queryParam("ubicacion", ubicacionAnden)
                .buildAndExpand(idContenedor)
                .toUriString();
                
        return restClient.put()
                .uri(uri)
                .retrieve()
                .body(ContenedorResponse.class);
    }
    
    // --- FALLBACKS ---
    
    private List<ContenedorResponse> obtenerContenedoresPatioFallback(Throwable t) {
        return List.of(); 
    }
}
