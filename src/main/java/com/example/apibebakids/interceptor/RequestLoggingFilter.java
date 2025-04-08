package com.example.apibebakids.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * Filter koji omogućava višestruko čitanje tela zahteva.
 * Mora biti registrovan sa najvišim prioritetom kako bi presreo zahteve pre drugih filtera.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Proveravamo da zahtev već nije omotan
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }

        // Proveravamo da odgovor već nije omotan
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }

        try {
            // Nastavi sa procesiranjem zahteva
            filterChain.doFilter(request, response);
        } finally {
            // Važno: kopirati sadržaj odgovora nazad pre nego što završimo
            // Ovo je neophodno za ContentCachingResponseWrapper
            if (response instanceof ContentCachingResponseWrapper) {
                ((ContentCachingResponseWrapper) response).copyBodyToResponse();
            }
        }
    }
}