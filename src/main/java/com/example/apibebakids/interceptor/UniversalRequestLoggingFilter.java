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
 * Filter koji omogućava višestruko čitanje tela zahteva za SVE zahteve u aplikaciji.
 * Visok prioritet osigurava da se izvrši pre ostalih komponenti.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UniversalRequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Omotavamo SVE zahteve, bez filtriranja po putanji
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // Nastavi sa procesiranjem zahteva
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // Važno: kopirati sadržaj odgovora nazad i otpustiti
            responseWrapper.copyBodyToResponse();
        }
    }
}