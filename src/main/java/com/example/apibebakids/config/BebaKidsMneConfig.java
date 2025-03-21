package com.example.apibebakids.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BebaKidsMneConfig {

    @Value("${spring.datasource.connBK.url}")
    private String url;

    @Value("${spring.datasource.connBKBIH.username}")
    private String username;

    @Value("${spring.datasource.connBKBIH.password}")
    private String password;

    @Value("${spring.datasource.connBKBIH.driver-class-name}")
    private String driverClassName;

    @Bean(name = "dataSourceBebaKidsMne")
    public DataSource dataSourceBebaKidsMne() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateBebaKidsMne")
    public JdbcTemplate jdbcTemplateBebaKidsMne() {
        return new JdbcTemplate(dataSourceBebaKidsMne());
    }
}