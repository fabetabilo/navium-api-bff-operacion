package com.navium.bff_operacion.client.dto;

public record AndenResponse(
    Long id,
    String zona,
    Integer numero,
    String codigo,
    String tipo,
    String estado,
    String sector
) {}
