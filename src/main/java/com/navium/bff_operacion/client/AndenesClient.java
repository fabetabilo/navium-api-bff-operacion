package com.navium.bff_operacion.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.navium.bff_operacion.client.dto.AndenResponse;
import com.navium.bff_operacion.client.dto.AsignacionResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class AndenesClient {
    
    private final RestClient restClient;
    
    public AndenesClient(RestClient.Builder restClientBuilder, @Value("${navium.ms.andenes.url}") String msUrl) {
        this.restClient = restClientBuilder.baseUrl(msUrl).build();
    }
    
    @CircuitBreaker(name = "andenesCb", fallbackMethod = "obtenerAndenesFallback")
    public List<AndenResponse> obtenerAndenes() {
        return restClient.get()
                .uri("/api/v0/andenes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AndenResponse>>() {});
    }
    
    @CircuitBreaker(name = "andenesCb", fallbackMethod = "asignarAndenFallback")
    public AsignacionResponse asignarAnden(Long idAnden, String patente, Long contenedorId) {
        String uri = UriComponentsBuilder.fromPath("/api/v0/andenes/{id}/asignar")
                .queryParam("patente", patente)
                .queryParam("contenedorId", contenedorId)
                .buildAndExpand(idAnden)
                .toUriString();
                
        return restClient.post()
                .uri(uri)
                .retrieve()
                .body(AsignacionResponse.class);
    }
    
    // --- FALLBACKS ---

    private List<AndenResponse> obtenerAndenesFallback(Throwable t) {
        return List.of(); 
    }

    private AsignacionResponse asignarAndenFallback(Long idAnden, String patente, Long contenedorId, Throwable t) {
        throw new RuntimeException("El servicio de Andenes no está disponible para realizar la asignación en este momento.");
    }
}