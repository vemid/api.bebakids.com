package com.example.apibebakids.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class GeoxConfig {

    @Value("${spring.datasource.connCF.url}")
    private String url;

    @Value("${spring.datasource.connCF.username}")
    private String username;

    @Value("${spring.datasource.connCF.password}")
    private String password;

    @Value("${spring.datasource.connCF.driver-class-name}")
    private String driverClassName;

    @Bean(name = "dataSourceGeox")
    public DataSource dataSourceGeox() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateGeox")
    public JdbcTemplate jdbcTemplateGeox() {
        return new JdbcTemplate(dataSourceGeox());
    }
}