package com.navium.bff_operacion.web.dto;

import java.time.LocalDateTime;

/**
 * Dto de respuesta payload para vista de operario
 * Su funcion es enviar cada agendamiento con la informacion necesaria para operador
 */
public record AgendamientoMapResponse(
    Long id,
    String patenteCamion,
    String rutChofer,
    String tipoOperacion,
    Long idContenedor,
    String codigoSigla,
    String estadoTATC,
    String estadoGeneral,
    String rutEmpresaTransporte,
    LocalDateTime bloqueInicio,
    LocalDateTime bloqueFin,
    String estado
) {}
