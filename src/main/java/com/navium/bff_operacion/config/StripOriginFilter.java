package com.navium.bff_operacion.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StripOriginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;

        HttpServletRequest sinOrigin = new HttpServletRequestWrapper(http) {
            @Override
            public String getHeader(String name) {
                if ("Origin".equalsIgnoreCase(name)) {
                    return null;
                }
                return super.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if ("Origin".equalsIgnoreCase(name)) {
                    return Collections.emptyEnumeration();
                }
                return super.getHeaders(name);
            }
        };

        chain.doFilter(sinOrigin, response);
    }
}