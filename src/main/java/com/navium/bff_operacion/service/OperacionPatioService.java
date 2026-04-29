package com.navium.bff_operacion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import com.navium.bff_operacion.client.AgendamientoClient;
import com.navium.bff_operacion.client.AndenesClient;
import com.navium.bff_operacion.client.ContenedoresClient;
import com.navium.bff_operacion.client.dto.AgendamientoResponse;
import com.navium.bff_operacion.client.dto.AndenResponse;
import com.navium.bff_operacion.client.dto.ContenedorResponse;
import com.navium.bff_operacion.web.dto.AgendamientoMapResponse;
import com.navium.bff_operacion.web.dto.AndenMapResponse;
import com.navium.bff_operacion.web.dto.AsignacionAndenRequest;

@Service
public class OperacionPatioService {
    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Santiago"); // zona horaria operativa del puerto local
    
    private final AndenesClient andenesClient;
    private final ContenedoresClient contenedoresClient;
    private final AgendamientoClient agendamientoClient;

    public OperacionPatioService(AndenesClient andenesClient, ContenedoresClient contenedoresClient, AgendamientoClient agendamientoClient) {
        this.andenesClient = andenesClient;
        this.contenedoresClient = contenedoresClient;
        this.agendamientoClient = agendamientoClient;
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
    
    public List<AgendamientoMapResponse> obtenerAgendamientosHoy() {
        LocalDate hoy = LocalDate.now(ZONA_HORARIA);
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(23, 59, 59);

        return agendamientoClient.listarPorFechas(inicio, fin).stream()
                .map(this::toOperarioResponse)
                .toList();
    }
    
    public List<AgendamientoMapResponse> buscarAgendamientosPorPatente(String patente) {
        return agendamientoClient.buscarPorPatente(patente).stream()
                .map(this::toOperarioResponse)
                .toList();
    }
    
    public AgendamientoMapResponse buscarAgendamientoPorId(Long id) {
        AgendamientoResponse agendamiento = obtenerAgendamientoSeguro(id);
        if (agendamiento == null) {
            return null;
        }
        return toOperarioResponse(agendamiento);
    }
    
    private AgendamientoMapResponse toOperarioResponse(AgendamientoResponse agendamiento) {
        return new AgendamientoMapResponse(
                agendamiento.id(),
                agendamiento.patenteCamion(),
                agendamiento.rutChofer(),
                agendamiento.tipoOperacion(),
                agendamiento.idContenedor(),
                agendamiento.bloqueInicio(),
                agendamiento.bloqueFin(),
                agendamiento.estado());
    }

    private AgendamientoResponse obtenerAgendamientoSeguro(Long id) {
        try {
            return agendamientoClient.buscarPorId(id);
        } catch (RestClientResponseException ex) {
            return null;
        }
    }
}
