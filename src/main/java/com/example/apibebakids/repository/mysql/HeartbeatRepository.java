package com.example.apibebakids.repository.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.apibebakids.model.mysql.DeviceHeartbeat;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class HeartbeatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HeartbeatRepository(@Qualifier("jdbcTemplateMysql") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getDevices(String station) {
        String sql = "SELECT id, device_id, shop_name, active, station, ip " +
                "FROM radio_bebakids.devices " +
                "WHERE station = ? and active = 1 " +
                "ORDER BY id";
        return jdbcTemplate.queryForList(sql, station);
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