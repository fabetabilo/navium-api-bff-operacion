package com.navium.bff_operacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navium.bff_operacion.service.OperacionPatioService;
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
}
