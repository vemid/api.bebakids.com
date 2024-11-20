package com.example.apibebakids.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
public class MySQLConfig {

    @Value("${spring.datasource.mysql.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.mysql.driver-class-name}")
    private String driverClassName;

    @Bean(name = "dataSourceMysql")
    public DataSource dataSourceMysql() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }


//    @Bean
//    public JdbcTemplate mysqlJdbcTemplate(DataSource mysqlDataSource) {
//        return new JdbcTemplate(mysqlDataSource);
//    }

    @Bean(name = "jdbcTemplateMysql")
    public JdbcTemplate mysqlJdbcTemplate() {
        return new JdbcTemplate(dataSourceMysql());
    }
}
