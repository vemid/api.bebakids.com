package com.example.apibebakids.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

@Component
public class CustomRequestLoggingInterceptor implements HandlerInterceptor {

    private final String logDirectory;

    // Konstruktor sa injekcijom putanje log direktorijuma
    public CustomRequestLoggingInterceptor(String logDirectory) {
        this.logDirectory = logDirectory;

        // Kreirajmo direktorijum ako ne postoji
        try {
            Path dir = Paths.get(logDirectory);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Ovde samo propuštamo zahtev dalje
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        // Generišemo naziv log fajla
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String method = request.getMethod();
        String uri = request.getRequestURI().replace("/", "_");

        // Bezbedno konstruišemo putanju log fajla
        Path logFilePath = Paths.get(logDirectory, "log_" + method + uri + "_" + timestamp + ".log");

        // Gradimo i upisujemo log
        StringBuilder logMessage = new StringBuilder();

        // Log osnovnih informacija
        logMessage.append("Request URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("Timestamp: ").append(LocalDateTime.now()).append("\n");
        logMessage.append("Client IP: ").append(request.getRemoteAddr()).append("\n");

        // Log zaglavlja zahteva
        logMessage.append("\n=== REQUEST HEADERS ===\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logMessage.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }

        // Log parametara
        logMessage.append("\n=== REQUEST PARAMETERS ===\n");
        Enumeration<String> paramNames = request.getParameterNames();
        if (!paramNames.hasMoreElements()) {
            logMessage.append("[No parameters]\n");
        } else {
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] values = request.getParameterValues(paramName);
                for (String value : values) {
                    logMessage.append(paramName).append("=").append(value).append("\n");
                }
            }
        }

        // Log tela zahteva
        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            logMessage.append("\n=== REQUEST BODY ===\n");

            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    String characterEncoding = wrapper.getCharacterEncoding();
                    if (characterEncoding == null) {
                        characterEncoding = StandardCharsets.UTF_8.name();
                    }
                    String payload = new String(buf, 0, buf.length, characterEncoding);
                    logMessage.append(payload).append("\n");
                } else {
                    logMessage.append("[Empty body or body already read]\n");
                }
            } else {
                logMessage.append("[Request body not available - not wrapped with ContentCachingRequestWrapper]\n");
            }
        }

        // Log zaglavlja odgovora
        logMessage.append("\n=== RESPONSE HEADERS ===\n");
        for (String headerName : response.getHeaderNames()) {
            logMessage.append(headerName).append(": ").append(response.getHeader(headerName)).append("\n");
        }

        // Log statusnog koda odgovora
        logMessage.append("\n=== RESPONSE STATUS ===\n");
        logMessage.append("Status: ").append(response.getStatus()).append("\n");

        // Log tela odgovora
        logMessage.append("\n=== RESPONSE BODY ===\n");
        if (response instanceof ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String characterEncoding = wrapper.getCharacterEncoding();
                if (characterEncoding == null) {
                    characterEncoding = StandardCharsets.UTF_8.name();
                }
                String payload = new String(buf, 0, buf.length, characterEncoding);
                logMessage.append(payload).append("\n");
            } else {
                logMessage.append("[Empty response body or not available]\n");
            }
        } else {
            logMessage.append("[Response body not available - not wrapped with ContentCachingResponseWrapper]\n");
        }

        // Zapišimo log u fajl
        try {
            Files.write(logFilePath, logMessage.toString().getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}