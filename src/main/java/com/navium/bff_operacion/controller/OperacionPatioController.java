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
	
	
	@PostMapping("/andenes/asignar")
	public ResponseEntity<Void> asignarAndenManual(@RequestBody AsignacionAndenRequest request) {
		try {
			this.operacionPatioService.asignarAndenManual(request);
			return ResponseEntity.ok().build();
            
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@GetMapping("/agendamientos/hoy")
	public ResponseEntity<List<AgendamientoMapResponse>> obtenerAgendamientosHoy() {
		List<AgendamientoMapResponse> agendamientos = this.operacionPatioService.obtenerAgendamientosHoy();
		if (agendamientos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(agendamientos);
	}
	
	@GetMapping("/agendamientos/patente/{patente}")
	public ResponseEntity<List<AgendamientoMapResponse>> buscarAgendamientosPorPatente(@PathVariable String patente) {
		List<AgendamientoMapResponse> agendamientos = this.operacionPatioService.buscarAgendamientosPorPatente(patente);
		if (agendamientos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(agendamientos);
	}
	
	@GetMapping("/agendamientos/{id}")
	public ResponseEntity<AgendamientoMapResponse> buscarAgendamientoPorId(@PathVariable Long id) {
		AgendamientoMapResponse agendamiento = this.operacionPatioService.buscarAgendamientoPorId(id);
		if (agendamiento == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(agendamiento);
	}
	
	/**
	 * Consulta desde puerto; permite consultar a traves de id de agendamiento y patente.
	 * Se utiliza patente para permitir la consulta de multiples contenedores para el mismo transporte.
	 */
	@GetMapping("/agendamientos/consulta")
	public ResponseEntity<?> consultarAgendamientos(@RequestParam(required = false) String patente, @RequestParam(required = false) Long id) {
		// si viene id, devuelve un solo agendamiento
		if (id != null) {
			AgendamientoMapResponse agendamiento = this.operacionPatioService.buscarAgendamientoPorId(id);
			if (agendamiento == null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(agendamiento);
		}
		// si viene patente devuelve lista de agendamientos asociados; por ej. transporte de doble contenedor
		if (patente != null && !patente.isBlank()) {
			List<AgendamientoMapResponse> agendamientos = this.operacionPatioService.buscarAgendamientosPorPatente(patente);
			if (agendamientos.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(agendamientos);
		}
		
		return ResponseEntity.badRequest().body("Debe indicar id o patente");
	}
}
