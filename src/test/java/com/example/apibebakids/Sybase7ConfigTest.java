package com.example.apibebakids;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootTest
public class Sybase7ConfigTest {

    @Autowired
    private JdbcTemplate jdbcTemplateSybase7;

    @Test
    public void testFetchAllWorkers() {
        String sql = "SELECT nazrad FROM radnici";
        List<String> workers = jdbcTemplateSybase7.queryForList(sql, String.class);
        assertNotNull(workers);
        workers.forEach(System.out::println);
    }
}