package com.navium.bff_operacion.client.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa la información de un Anden junto con su asignación activa.
 * 
 * Este record se utiliza para recibir la respuesta del microservicio de andenes
 * cuando se consulta un andén con su asignación actual.
 * 
 * @param codigo Identificador compuesto del andén (zona + número), ej: "A5"
 * @param tipo Tipo de andén (ej: "CARGA", "DESCARGA")
 * @param estado Estado actual del andén (ej: "DISPONIBLE", "OCUPADO", "MANTENIMIENTO")
 * @param asignacionId ID de la asignación activa, null si el andén está libre
 * @param patenteTransporte Patente del transporte asignado, null si no hay asignación
 * @param contenedorId ID del contenedor asignado, null si no hay asignación
 * @param horaInicio Fecha y hora de inicio de la asignación, null si no hay asignación
 * @param horaFin Fecha y hora de fin de la asignación, null si la asignación está activa
 */
public record AndenInformacionResponse(
    String codigo,
    String tipo,
    String estado,
    Long asignacionId,
    String patenteTransporte,
    Long contenedorId,
    LocalDateTime horaInicio,
    LocalDateTime horaFin
) {}
