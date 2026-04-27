package com.navium.bff_operacion.client.dto;

import java.time.LocalDateTime;

public record AsignacionResponse(
    Long id,
    Long andenId,
    String patenteTransporte,
    Long contenedorId,
    LocalDateTime horaInicio,
    LocalDateTime horaFin
) {}
