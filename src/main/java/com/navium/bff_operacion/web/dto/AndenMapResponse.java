package com.navium.bff_operacion.web.dto;

/**
 * Dto de respuesta payload para vista de usuario.
 * Su funcion es enviar cada anden con la informacion necesaria para operador; 
 * ubicacion real y si tiene contenedor asociado o no.
 */
public record AndenMapResponse(
    Long idAnden,
    String codigoAnden,
    String zona,
    Integer numero,
    String tipo,
    String estado,
    String sector,
    // informacion del contenedor si es que hay uno asignado. puede ser null si anden esta DISPONIBLE
    Long idContenedor,
    String codigoSiglaContenedor,
    String tipoCargaContenedor
) {}
