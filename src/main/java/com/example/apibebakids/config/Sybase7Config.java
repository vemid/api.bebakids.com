package com.example.apibebakids.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class Sybase7Config {

    @Value("${spring.datasource.connSybase7.url}")
    private String url;

    @Value("${spring.datasource.connSybase7.username}")
    private String username;

    @Value("${spring.datasource.connSybase7.password}")
    private String password;

    @Value("${spring.datasource.connSybase7.driver-class-name}")
    private String driverClassName;

    @Bean(name = "dataSourceSybase7")
    public DataSource dataSourceSybase7() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateSybase7")
    public JdbcTemplate jdbcTemplateSybase7() {
        return new JdbcTemplate(dataSourceSybase7());
    }
}