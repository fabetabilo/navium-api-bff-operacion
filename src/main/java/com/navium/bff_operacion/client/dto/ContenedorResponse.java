package com.navium.bff_operacion.client.dto;

// en revision
public record ContenedorResponse(
    Long id,
    String codigoSigla,
    String tipoCarga,
    String rutEmpresaTransporte,
    String estadoBL,
    String estadoTATC,
    String ubicacionAnden
) {}
