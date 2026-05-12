package com.navium.bff_operacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.navium.bff_operacion.client.dto.AndenInformacionResponse;
import com.navium.bff_operacion.service.OperacionPatioService;
import com.navium.bff_operacion.web.dto.AgendamientoMapResponse;
import com.navium.bff_operacion.web.dto.AndenMapResponse;
import com.navium.bff_operacion.web.dto.AsignacionAndenRequest;

@RestController
@RequestMapping("/api/v0/operacion")
public class OperacionPatioController {
    
	private final OperacionPatioService operacionPatioService;
    
	public OperacionPatioController(OperacionPatioService operacionPatioService) {
		this.operacionPatioService = operacionPatioService;
	}
    
	@GetMapping("/andenes")
	public ResponseEntity<List<AndenMapResponse>> obtenerMapaAndenes() {
		List<AndenMapResponse> andenes = this.operacionPatioService.obtenerMapaAndenes();
        if (andenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(andenes);
	}

	/**
	 * Obtiene todos los andenes ocupados con su información de asignación activa.
	 * Consume el endpoint del microservicio de andenes que incluye la asignación.
	 */
	@GetMapping("/andenes/info")
	public ResponseEntity<List<AndenInformacionResponse>> obtenerAndenesConAsignacionInfo() {
		List<AndenInformacionResponse> andenes = this.operacionPatioService.obtenerAndenesConAsignacionInfo();
		if (andenes.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(andenes);
	}

	/**
	 * Obtiene un andén específico con su información de asignación activa.
	 * Consume el endpoint del microservicio de andenes que incluye la asignación.
	 */
	@GetMapping("/andenes/{id}/info")
	public ResponseEntity<AndenInformacionResponse> obtenerAndenConAsignacionInfo(@PathVariable Long id) {
		AndenInformacionResponse anden = this.operacionPatioService.obtenerAndenConAsignacionInfo(id);
		return ResponseEntity.ok(anden);
	}

	@PostMapping("/andenes/asignar")
	public ResponseEntity<String> asignarAndenManual(@RequestBody AsignacionAndenRequest request) {
		try {
			this.operacionPatioService.asignarAndenManual(request);
			return ResponseEntity.ok().build();
            
		} catch (Exception e) {
			// log temporal!!! para diagnostico, prueba de circuit breaker
        	System.out.println("ERROR en asignarAndenManual: " + e.getMessage());
        	e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	/* FUTURO:
	@GetMapping("/agendamientos/hoy")
	public ResponseEntity<List<AgendamientoMapResponse>> obtenerAgendamientosHoy() {
		List<AgendamientoMapResponse> agendamientos = this.operacionPatioService.obtenerAgendamientosHoy();
		if (agendamientos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(agendamientos);
	}
	*/
	
	/**
	 * 
	 */
	@GetMapping("/agendamientos/patente/{patente}")
	public ResponseEntity<List<AgendamientoMapResponse>> buscarAgendamientosPorPatente(@PathVariable String patente) {
		List<AgendamientoMapResponse> agendamientos = this.operacionPatioService.buscarAgendamientosPorPatente(patente);
		if (agendamientos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(agendamientos);
	}
	
	/**
	 * Consulta desde puerto; permite consultar a traves de id de agendamiento y patente.
	 * Se utiliza patente para permitir la consulta de multiples contenedores para el mismo transporte.
	 * El parámetro momento es opcional y permite consultas históricas o futuras (formato ISO-8601).
	 */
	@GetMapping("/agendamientos/consulta")
	public ResponseEntity<?> consultarAgendamientos(
			@RequestParam(required = false) String patente,
			@RequestParam(required = false) Long id,
			@RequestParam(required = false) String momento) {
		AgendamientoMapResponse agendamiento = this.operacionPatioService.consultarAgendamiento(patente, id, momento);
		if (agendamiento == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(agendamiento);
	}
}
