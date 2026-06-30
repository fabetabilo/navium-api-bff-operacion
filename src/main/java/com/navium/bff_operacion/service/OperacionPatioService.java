package com.navium.bff_operacion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.navium.bff_operacion.client.AgendamientoClient;
import com.navium.bff_operacion.client.AndenesClient;
import com.navium.bff_operacion.client.ContenedoresClient;
import com.navium.bff_operacion.client.dto.AgendamientoResponse;
import com.navium.bff_operacion.client.dto.AndenInformacionResponse;
import com.navium.bff_operacion.client.dto.AndenResponse;
import com.navium.bff_operacion.client.dto.ContenedorResponse;
import com.navium.bff_operacion.web.dto.AgendamientoMapResponse;
import com.navium.bff_operacion.web.dto.AndenMapResponse;
import com.navium.bff_operacion.web.dto.AsignacionAndenRequest;

@Service
public class OperacionPatioService {
    
    private final AndenesClient andenesClient;
    private final AgendamientoClient agendamientoClient;
    private final ContenedoresClient contenedoresClient;

    public OperacionPatioService(AndenesClient andenesClient, AgendamientoClient agendamientoClient, ContenedoresClient contenedoresClient) {
        this.andenesClient = andenesClient;
        this.agendamientoClient = agendamientoClient;
        this.contenedoresClient = contenedoresClient;
    }

    /**
     * Orquesta informacion de andenes.
     * Obtiene los andenes del microservicio de andenes.
     */
    public List<AndenMapResponse> obtenerMapaAndenes() {
        List<AndenResponse> andenes = andenesClient.obtenerAndenes();
                
        return andenes.stream().map(adn -> new AndenMapResponse(
            adn.id(),
            adn.codigo(),
            adn.zona(),
            adn.numero(),
            adn.tipo(),
            adn.estado(),
            adn.sector()
        )).toList();
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
        
        andenesClient.asignarAnden(request.idAnden(), request.patenteTransporte(), request.idContenedor());
    }
    /*
    public List<AgendamientoMapResponse> obtenerAgendamientosHoy() {
        LocalDate hoy = LocalDate.now(ZONA_HORARIA);
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(23, 59, 59);

        return agendamientoClient.listarPorFechas(inicio, fin).stream()
                .map(this::enriquecerConContenedor)
                .toList();
    }
    */
    
    public List<AgendamientoMapResponse> buscarAgendamientosPorPatente(String patente) {
        return agendamientoClient.buscarPorPatente(patente).stream()
                .map(this::enriquecerConContenedor)
                .toList();
    }

    /**
     * Consulta semántica de agendamiento para "en puerta".
     * - Si viene id: retorna ese agendamiento.
     * - Si viene patente: retorna el agendamiento vigente para el momento, o el próximo futuro más cercano.
     * - El parámetro momento es opcional; si no se proporciona, usa el tiempo actual.
     */
    public AgendamientoMapResponse consultarAgendamiento(String patente, Long id, String momento) {
        AgendamientoResponse agendamiento = agendamientoClient.consultar(patente, id, momento);
        if (agendamiento == null) {
            return null;
        }
        return enriquecerConContenedor(agendamiento);
    }

    /**
     * Orquesta datos del agendamiento con información del contenedor desde el MS Contenedores.
     * Si el contenedorId es null o el MS Contenedores no responde, se envían los campos del contenedor como null.
     */
    private AgendamientoMapResponse enriquecerConContenedor(AgendamientoResponse agendamiento) {
        ContenedorResponse contenedor = null;
        if (agendamiento.contenedorId() != null) {
            contenedor = contenedoresClient.obtenerContenedorPorId(agendamiento.contenedorId());
        }
        return toOperarioResponse(agendamiento, contenedor);
    }

    private AgendamientoMapResponse toOperarioResponse(AgendamientoResponse ag, ContenedorResponse cont) {
        return new AgendamientoMapResponse(
                ag.id(),
                ag.patenteCamion(),
                ag.rutChofer(),
                ag.tipoOperacion(),
                ag.contenedorId(),
                cont != null ? cont.codigoSigla() : null,
                cont != null ? cont.estadoTATC() : null,
                cont != null ? cont.estadoGeneral() : null,
                cont != null ? cont.rutEmpresaTransporte() : null,
                ag.horaInicio(),
                ag.bloqueFin(),
                ag.estadoAgendamiento());
    }

    /**
     * Obtiene todos los andenes ocupados con su información de asignación activa.
     * Consume el endpoint del microservicio de andenes que incluye la asignación.
     */
    public List<AndenInformacionResponse> obtenerAndenesConAsignacionInfo() {
        return andenesClient.obtenerAndenesConAsignacion();
    }

    /**
     * Obtiene un andén específico con su información de asignación activa.
     * Consume el endpoint del microservicio de andenes que incluye la asignación.
     */
    public AndenInformacionResponse obtenerAndenConAsignacionInfo(Long idAnden) {
        return andenesClient.obtenerAndenConAsignacion(idAnden);
    }

    /*
    private AgendamientoResponse obtenerAgendamientoSeguro(Long id) {
        try {
            return agendamientoClient.buscarPorId(id);
        } catch (RestClientResponseException ex) {
            return null;
        }
    }
    */    
}

