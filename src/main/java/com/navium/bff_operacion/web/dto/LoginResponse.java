package com.navium.bff_operacion.web.dto;
 
/**
 * Payload de login devuelto por el BFF al frontend.
 *
 * El token es un JWT emitido por el microservicio de Usuarios. El BFF solo
 * lo reenvia y no lo almacena. El frontend debe guardarlo (por ejemplo, en
 * cookie HttpOnly o memoria) y enviarlo en las siguientes solicitudes.
 */
public record LoginResponse(
    String token
) {}