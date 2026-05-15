package com.navium.bff_operacion.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.navium.bff_operacion.client.dto.AgendamientoResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class AgendamientoClient {

    private final RestClient restClient;

    public AgendamientoClient(RestClient.Builder restClientBuilder, @Value("${navium.ms.agendamiento.url}") String msUrl) {
        this.restClient = restClientBuilder.baseUrl(msUrl).build();
    }

    @CircuitBreaker(name = "agendamientoCb", fallbackMethod = "listarPorFechasFallback")
    public List<AgendamientoResponse> listarPorFechas(LocalDateTime inicio, LocalDateTime fin) {
        String uri = UriComponentsBuilder.fromPath("/api/agendamientos/fechas")
                .queryParam("inicio", inicio)
                .queryParam("fin", fin)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AgendamientoResponse>>() {});
    }

    @CircuitBreaker(name = "agendamientoCb", fallbackMethod = "buscarPorPatenteFallback")
    public List<AgendamientoResponse> buscarPorPatente(String patente) {
        return restClient.get()
                .uri("/api/agendamientos/patente/{patente}", patente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AgendamientoResponse>>() {});
    }

    @CircuitBreaker(name = "agendamientoCb", fallbackMethod = "buscarPorIdFallback")
    public AgendamientoResponse buscarPorId(Long id) {
        return restClient.get()
                .uri("/api/agendamientos/{id}", id)
                .retrieve()
                .body(AgendamientoResponse.class);
    }

    @CircuitBreaker(name = "agendamientoCb", fallbackMethod = "consultarFallback")
    public AgendamientoResponse consultar(String patente, Long id, String momento) {
        String uri = UriComponentsBuilder.fromPath("/api/agendamientos/consulta")
                .queryParam("patente", patente)
                .queryParam("id", id)
                .queryParam("momento", momento)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(AgendamientoResponse.class);
    }

    // --- FALLBACKS ---

    private List<AgendamientoResponse> listarPorFechasFallback(LocalDateTime inicio, LocalDateTime fin, Throwable t) {
        return List.of();
    }

    private List<AgendamientoResponse> buscarPorPatenteFallback(String patente, Throwable t) {
        return List.of();
    }

    private AgendamientoResponse buscarPorIdFallback(Long id, Throwable t) {
        return null;
    }

    private AgendamientoResponse consultarFallback(String patente, Long id, String momento, Throwable t) {
        return null;
    }
}
