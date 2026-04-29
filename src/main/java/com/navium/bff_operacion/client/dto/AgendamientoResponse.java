package com.navium.bff_operacion.client.dto;

import java.time.LocalDateTime;

public record AgendamientoResponse(
    Long id,
    Long usuarioId,
    String patenteCamion,
    String rutChofer,
    String tipoOperacion,
    String idContenedor,
    LocalDateTime bloqueInicio,
    LocalDateTime bloqueFin,
    String estado
) {}
