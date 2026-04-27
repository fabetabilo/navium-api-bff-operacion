package com.navium.bff_operacion.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.navium.bff_operacion.client.dto.ContenedorResponse;

@Component
public class ContenedoresClient {
    
    private final RestClient restClient;
    
    public ContenedoresClient(RestClient.Builder restClientBuilder, @Value("${navium.ms.contenedores.url}") String msUrl) {
        this.restClient = restClientBuilder.baseUrl(msUrl).build();
    }
    
    public List<ContenedorResponse> obtenerContenedoresPatio() {
        return restClient.get()
                .uri("/api/contenedores/patio")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ContenedorResponse>>() {});
    }
    
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
}
