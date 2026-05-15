package com.navium.bff_operacion.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.navium.bff_operacion.client.dto.AndenInformacionResponse;
import com.navium.bff_operacion.client.dto.AndenResponse;
import com.navium.bff_operacion.client.dto.AsignacionAndenResponse;

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
    public AsignacionAndenResponse asignarAnden(Long idAnden, String patente, Long contenedorId) {
        String uri = UriComponentsBuilder.fromPath("/api/v0/andenes/{id}/asignar")
                .queryParam("patente", patente)
                .queryParam("contenedorId", contenedorId)
                .buildAndExpand(idAnden)
                .toUriString();
                
        return restClient.post()
                .uri(uri)
                .retrieve()
                .body(AsignacionAndenResponse.class);
    }
    
    @CircuitBreaker(name = "andenesCb", fallbackMethod = "obtenerAndenesConAsignacionFallback")
    public List<AndenInformacionResponse> obtenerAndenesConAsignacion() {
        return restClient.get()
                .uri("/api/v0/andenes/asignacion")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AndenInformacionResponse>>() {});
    }

    @CircuitBreaker(name = "andenesCb", fallbackMethod = "obtenerAndenConAsignacionFallback")
    public AndenInformacionResponse obtenerAndenConAsignacion(Long idAnden) {
        return restClient.get()
                .uri("/api/v0/andenes/{id}/asignacion", idAnden)
                .retrieve()
                .body(AndenInformacionResponse.class);
    }

    // --- FALLBACKS ---

    private List<AndenResponse> obtenerAndenesFallback(Throwable t) {
        return List.of(); 
    }

    private AsignacionAndenResponse asignarAndenFallback(Long idAnden, String patente, Long contenedorId, Throwable t) {
        throw new RuntimeException("El servicio de Andenes no está disponible para realizar la asignación en este momento.");
    }

    private List<AndenInformacionResponse> obtenerAndenesConAsignacionFallback(Throwable t) {
        return List.of(); 
    }

    private AndenInformacionResponse obtenerAndenConAsignacionFallback(Long idAnden, Throwable t) {
        throw new RuntimeException("El servicio de Andenes no está disponible para obtener la información del andén.");
    }
}