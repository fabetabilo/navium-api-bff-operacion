package com.navium.bff_operacion.client.dto;

public record ContenedorResponse(
    Long id,
    String codigoSigla,
    String tipoCarga,
    String rutEmpresaTransporte,
    String estadoBL,
    String estadoTATC,
    String ubicacionAnden
) {}
