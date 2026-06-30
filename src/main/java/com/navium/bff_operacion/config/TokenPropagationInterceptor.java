package com.navium.bff_operacion.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * Interceptor para propagar el token JWT del BFF a los microservicios downstream.
 * 
 * Este interceptor:
 * - Extrae el token JWT del header Authorization de la request HTTP actual
 * - Lo agrega al header Authorization de las requests que se hacen a los microservicios downstream
 * - Permite que los microservicios validen el token del usuario original
 */
@Component
public class TokenPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        
        // Obtener la request HTTP actual del contexto de Spring
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest currentRequest = attributes.getRequest();

            // Auth por cookie httpOnly: reenviar la cookie de sesión a los microservicios
            String cookie = currentRequest.getHeader("Cookie");
            if (cookie != null && !cookie.isEmpty()) {
                request.getHeaders().add("Cookie", cookie);
            }

            // Compatibilidad: si llegara un Authorization, también propagarlo
            String authorizationHeader = currentRequest.getHeader("Authorization");
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                request.getHeaders().add("Authorization", authorizationHeader);
            }
        }
        
        // Continuar con la ejecución de la request
        return execution.execute(request, body);
    }
}
