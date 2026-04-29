package com.navium.bff_operacion.web.dto;

import java.time.LocalDateTime;

/**
 * Dto para la asignacion manual en tiempo real para andenes.
 * (aquellos que no tengan agendamiento)
 */
public record AsignacionAndenRequest(
    Long idAnden,
    String codigoAnden,
    String patenteTransporte,
    Long idContenedor,
    // futuras validaciones de solapamiento de horario contra el microservicio agendamiento
    LocalDateTime horaInicioEstimada,
    LocalDateTime horaFinEstimada
) {}
