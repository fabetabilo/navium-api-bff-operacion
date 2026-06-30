package com.navium.bff_operacion.client.dto;

import java.time.LocalDateTime;

public record AgendamientoResponse(
    Long id,
    Long idUsuario,
    String patenteCamion,
    String rutChofer,
    String tipoOperacion,
    Long contenedorId,
    String correoUsuario,
    LocalDateTime horaInicio,
    LocalDateTime bloqueFin,
    String estadoAgendamiento
) {}
