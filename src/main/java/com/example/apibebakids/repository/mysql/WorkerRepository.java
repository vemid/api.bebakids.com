package com.example.apibebakids.repository.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import com.example.apibebakids.model.mysql.ProductionWorker;
import com.example.apibebakids.model.mysql.ProductionWorkerCheckin;
import org.springframework.dao.EmptyResultDataAccessException;

@Component
public class WorkerRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkerRepository(@Qualifier("jdbcTemplateMysql") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductionWorker> findAllWorkers(String locationId) {
        String sql = "SELECT id, CONCAT(name, ' ', last_name) AS name, checkin_code AS employeeCode FROM production_workers WHERE location_code_id = ? and checkin_code is not null";
        return jdbcTemplate.query(
                sql,
                new Object[] { locationId }, // This will pass locationId into the SQL query
                (rs, rowNum) -> new ProductionWorker(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("employeeCode")
                )
        );
    }

    public void saveCheckin(ProductionWorkerCheckin checkin, Long workerID) {

        jdbcTemplate.update(
                "INSERT INTO production_worker_checkins (worker_id, date, check_in, check_out) VALUES (?, ?, ?, ?)",
                workerID, checkin.getCheckinDate(), checkin.getCheckinTime(), checkin.getCheckinEndTime()
        );
    }

    public Long findWorkerIdByCode(String workerCode) {
        String sql = "SELECT id FROM production_workers WHERE checkin_code = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{workerCode}, Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null; // or handle exception as per your application's requirement
        }
    }
}
