package com.example.apibebakids.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

@Component
public class CustomRequestLoggingInterceptor implements HandlerInterceptor {

    private String logDirectory;

    // Constructor to accept the log directory path
    public CustomRequestLoggingInterceptor(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Wrap the request in ContentCachingRequestWrapper to cache the request body
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String method = request.getMethod();
        String uri = request.getRequestURI().replace("/", "_");

        // Construct the log file path safely
        Path logFilePath = Paths.get(logDirectory, "log_" + method + uri + "_" + timestamp + ".log");

        // Log the request details
        logToFile(logFilePath.toString(), buildRequestLog((ContentCachingRequestWrapper) request));

        return true;
    }

    private String buildRequestLog(ContentCachingRequestWrapper request) throws IOException {
        StringBuilder logMessage = new StringBuilder();

        // Log Request URI and Method
        logMessage.append("Request URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");

        // Log User-Agent (Device Information)
        String userAgent = request.getHeader("User-Agent");
        logMessage.append("User-Agent: ").append(userAgent != null ? userAgent : "Unknown").append("\n");

        // Log Request Parameters (for GET and POST)
        logMessage.append("Parameters: ").append(getRequestParams(request)).append("\n");

        // Log Request Body (for POST)
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            logMessage.append("Body: ").append(getRequestBody(request)).append("\n");
        }

        return logMessage.toString();
    }

    private String getRequestParams(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.append(paramName).append("=").append(paramValue).append("&");
        }
        if (params.length() > 0) {
            params.setLength(params.length() - 1); // Remove trailing '&'
        }
        return params.toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) throws IOException {
        byte[] content = request.getContentAsByteArray();
        return content.length > 0 ? new String(content, request.getCharacterEncoding()) : "";
    }

    private void logToFile(String fileName, String message) {
        try {
            Files.write(Paths.get(fileName), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
    }
}
