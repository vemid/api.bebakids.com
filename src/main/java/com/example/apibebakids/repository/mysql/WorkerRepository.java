package com.example.apibebakids.repository.mysql;

import com.example.apibebakids.model.mysql.ProductionWorkerSyncCheckins;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

    public List<ProductionWorkerSyncCheckins> findAllChekinsByLocations(String locationId) {
        String sql = "select p.id,p.checkin_code,concat(p.name,\" \",p.last_name) worker_name,pw.date,pw.check_in,pw.check_out from production_worker_checkins pw left join production_workers p on p.id = pw.worker_id where date >= date(now())-1 and p.location_code_id = ?";
        return jdbcTemplate.query(
                sql,
                new Object[] { locationId },
                (rs, rowNum) -> new ProductionWorkerSyncCheckins(
                        rs.getLong("id"),
                        rs.getString("checkin_code"),
                        rs.getString("worker_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("check_in").toLocalTime(),
                        rs.getTime("check_out") != null ? rs.getTime("check_out").toLocalTime() : null
                )
        );
    }

    public void saveCheckin(ProductionWorkerCheckin checkin, Long workerID) {

        if (!checkForCheckIn(workerID, checkin.getCheckinDate())) {
            jdbcTemplate.update(
                    "INSERT INTO production_worker_checkins (worker_id, date, check_in, check_out) VALUES (?, ?, ?, ?)",
                    workerID, checkin.getCheckinDate(), checkin.getCheckinTime(), checkin.getCheckinEndTime()
            );
        } else {
            if (checkin.getCheckinEndTime() != null) {
                jdbcTemplate.update(
                        "UPDATE production_worker_checkins SET check_out = ? WHERE date = ? AND worker_id = ?",
                        checkin.getCheckinEndTime(), checkin.getCheckinDate(), workerID
                );
            }
        }
    }

    public boolean checkForCheckIn(Long workerId, LocalDate checkinDate) {
        String sql = "SELECT COUNT(*) FROM production_worker_checkins WHERE worker_id = ? AND date = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{workerId, checkinDate}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
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
