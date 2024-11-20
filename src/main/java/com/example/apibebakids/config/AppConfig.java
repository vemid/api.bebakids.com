package com.example.apibebakids.config;

import com.example.apibebakids.interceptor.CustomRequestLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    // Inject log file path from application.properties
    @Value("${log.file.path}")
    private String logFilePath;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CustomRequestLoggingInterceptor customRequestLoggingInterceptor() {
        return new CustomRequestLoggingInterceptor(logFilePath);  // Pass the log file path to the interceptor
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customRequestLoggingInterceptor()).addPathPatterns("/api/**");
    }
}
