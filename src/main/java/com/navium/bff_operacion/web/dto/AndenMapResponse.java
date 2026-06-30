package com.navium.bff_operacion.web.dto;

/**
 * Dto de respuesta payload para vista de usuario.
 * Su funcion es enviar cada anden con la informacion necesaria para operador; 
 * ubicacion real del anden.
 */
public record AndenMapResponse(
    Long idAnden,
    String codigoAnden,
    String zona,
    Integer numero,
    String tipo,
    String estado,
    String sector
) {}
