package com.example.apibebakids.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BebaKidsBihConfig {

    @Value("${spring.datasource.connBKBIH.url}")
    private String url;

    @Value("${spring.datasource.connBKBIH.username}")
    private String username;

    @Value("${spring.datasource.connBKBIH.password}")
    private String password;

    @Value("${spring.datasource.connBKBIH.driver-class-name}")
    private String driverClassName;

    @Bean(name = "dataSourceBebaKidsBih")
    public DataSource dataSourceBebaKidsBih() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateBebaKidsBih")
    public JdbcTemplate jdbcTemplateBebaKidsBih() {
        return new JdbcTemplate(dataSourceBebaKidsBih());
    }
}