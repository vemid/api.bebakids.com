package com.example.apibebakids.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class WatchConfig {

    @Value("${spring.datasource.connWATCH.url}")
    private String url;

    @Value("${spring.datasource.connWATCH.username}")
    private String username;

    @Value("${spring.datasource.connWATCH.password}")
    private String password;

    @Value("${spring.datasource.connWATCH.driver-class-name}")
    private String driverClassName;

    @Bean(name = "dataSourceWatch")
    public DataSource dataSourceWatch() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateWatch")
    public JdbcTemplate jdbcTemplateWatch() {
        return new JdbcTemplate(dataSourceWatch());
    }
}