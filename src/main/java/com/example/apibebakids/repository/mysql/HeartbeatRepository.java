package com.example.apibebakids.repository.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.apibebakids.model.mysql.DeviceHeartbeat;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Repository
public class HeartbeatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HeartbeatRepository(@Qualifier("jdbcTemplateMysql") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsHeartbeatForToday(String deviceId) {
        String sql = "SELECT COUNT(*) FROM radio_bebakids.device_heartbeats " +
                "WHERE device_id = ? AND DATE(timestamp) = CURRENT_DATE()";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, deviceId);
        return count != null && count > 0;
    }

    public void updateHeartbeat(String deviceId, LocalDateTime timestamp) {
        String sql = "UPDATE radio_bebakids.device_heartbeats SET timestamp = ? " +
                "WHERE device_id = ? AND DATE(timestamp) = CURRENT_DATE()";
        jdbcTemplate.update(sql, timestamp, deviceId);
    }

    public void saveHeartbeat(DeviceHeartbeat heartbeat) {
        String sql = "INSERT INTO radio_bebakids.device_heartbeats (device_id, timestamp) VALUES (?, ?)";
        jdbcTemplate.update(sql, heartbeat.getDeviceId(), heartbeat.getTimestamp());
    }
}