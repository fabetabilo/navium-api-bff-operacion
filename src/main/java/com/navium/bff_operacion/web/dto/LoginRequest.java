package com.navium.bff_operacion.web.dto;

/**
 * Payload de login enviado desde el frontend al BFF.
 *
 * El BFF reenvia estas credenciales al microservicio de Usuarios y no
 * persiste estado de sesion. El frontend es responsable de almacenar el JWT
 * devuelto tras el login.
 */
public record LoginRequest(
    String email,
    String password
) {}