package com.navium.bff_operacion.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.navium.bff_operacion.client.dto.AndenResponse;
import com.navium.bff_operacion.client.dto.AsignacionResponse;

@Component
public class AndenesClient {
    
    private final RestClient restClient;
    
    public AndenesClient(RestClient.Builder restClientBuilder, @Value("${navium.ms.andenes.url}") String msUrl) {
        this.restClient = restClientBuilder.baseUrl(msUrl).build();
    }
    
    public List<AndenResponse> obtenerAndenes() {
        return restClient.get()
                .uri("/api/v0/andenes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AndenResponse>>() {});
    }
    
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
}