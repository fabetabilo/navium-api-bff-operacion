package com.navium.bff_operacion.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.navium.bff_operacion.client.AndenesClient;
import com.navium.bff_operacion.client.ContenedoresClient;
import com.navium.bff_operacion.client.dto.AndenResponse;
import com.navium.bff_operacion.client.dto.ContenedorResponse;
import com.navium.bff_operacion.web.dto.AndenMapResponse;
import com.navium.bff_operacion.web.dto.AsignacionAndenRequest;

@Service
public class OperacionPatioService {

    private final AndenesClient andenesClient;
    private final ContenedoresClient contenedoresClient;

    public OperacionPatioService(AndenesClient andenesClient, ContenedoresClient contenedoresClient) {
        this.andenesClient = andenesClient;
        this.contenedoresClient = contenedoresClient;
    }

    /**
     * Orquesta informacion completa de andenes. 
     * Obtiene los andenes y los los contenedores que se encuentren registrados en el patio
     */
    public List<AndenMapResponse> obtenerMapaAndenes() {
        List<AndenResponse> andenes = andenesClient.obtenerAndenes();
        List<ContenedorResponse> contenedoresPatio = contenedoresClient.obtenerContenedoresPatio();
        
        Map<String, ContenedorResponse> contenedoresIndexados = contenedoresPatio.stream()
                .filter(con -> con.ubicacionAnden() != null && !con.ubicacionAnden().isEmpty())
                .collect(Collectors.toMap(
                        ContenedorResponse::ubicacionAnden,  // key: A1
                        con -> con,                          // valor de contenedor
                        (con1, con2) -> con1 // temporal!!: me hace ruido. entonces si hay un choque en base de datos, mantiene el primero
                ));
                
        return andenes.stream().map(adn -> {
            ContenedorResponse con = contenedoresIndexados.get(adn.codigo());
            return new AndenMapResponse(
                adn.id(),
                adn.codigo(),
                adn.zona(),
                adn.numero(),
                adn.tipo(),
                adn.estado(),
                adn.sector(),
                // REGLA: si existe un contenedor asignado mapeamos sus properties, sino -> null
                con != null ? con.id() : null,
                con != null ? con.codigoSigla() : null,
                con != null ? con.tipoCarga() : null
            );
        }).toList();
    }

    /**
     * Orquesta asignacion manual de contenedor a anden sin agendamiento previa
     */
    public void asignarAndenManual(AsignacionAndenRequest request) {
        /**
         * en el futuro se valida con microservicio Agendamiento segun hora de inicio estimada
         * y hora de finalizacion estimada para asegurar que no pise un turno que va a llegar pronto, que si 
         * tenga agendamiento
        */
        
        // REVISAR!!!! -> .actualizarAnden
        contenedoresClient.actualizarAnden(request.idContenedor(), request.codigoAnden());
        
        andenesClient.asignarAnden(request.idAnden(), request.patenteTransporte(), request.idContenedor());
    }
}
