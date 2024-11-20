package com.example.apibebakids.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
public class BebaKidsConfig {

    @Value("${spring.datasource.connBK.url}")
    private String url;

    @Value("${spring.datasource.connBK.username}")
    private String username;

    @Value("${spring.datasource.connBK.password}")
    private String password;

    @Value("${spring.datasource.connBK.driver-class-name}")
    private String driverClassName;

    @Bean(name = "dataSourceBebaKids")
    public DataSource dataSourceBebaKids() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateBebaKids")
    public JdbcTemplate jdbcTemplateBebaKids() {
        return new JdbcTemplate(dataSourceBebaKids());
    }
}