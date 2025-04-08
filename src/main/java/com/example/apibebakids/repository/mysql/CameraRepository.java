package com.example.apibebakids.repository.mysql;

import com.example.apibebakids.model.mysql.CameraHeartbeat;
import com.example.apibebakids.model.mysql.CameraData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class CameraRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CameraRepository(@Qualifier("jdbcTemplateMysql") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Provera da li postoji kamera sa datim serijskim brojem
     */
    public boolean existsCamera(String serialNumber) {
        String sql = "SELECT COUNT(*) FROM radio_bebakids.cameras WHERE serial_number = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serialNumber);
        return count != null && count > 0;
    }

    /**
     * Čuvanje novog heartbeat zapisa
     */
    public void saveHeartbeat(CameraHeartbeat heartbeat) {
        String sql = "INSERT INTO radio_bebakids.camera_heartbeats " +
                "(serial_number, timestamp, device_time, timezone_hours, upload_interval) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                heartbeat.getSerialNumber(),
                Timestamp.valueOf(heartbeat.getTimestamp()),
                heartbeat.getDeviceTime(),
                heartbeat.getTimezoneHours(),
                heartbeat.getUploadInterval());
    }

    /**
     * Čuvanje novog zapisa podataka kamere
     */
    public void saveCameraData(CameraData cameraData) {
        String sql = "INSERT INTO radio_bebakids.camera_data " +
                "(serial_number, timestamp, start_time, end_time, in_count, out_count, " +
                "passby_count, turnback_count, avg_stay_time, event_list_json) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                cameraData.getSerialNumber(),
                Timestamp.valueOf(cameraData.getTimestamp()),
                cameraData.getStartTime(),
                cameraData.getEndTime(),
                cameraData.getInCount(),
                cameraData.getOutCount(),
                cameraData.getPassByCount(),
                cameraData.getTurnBackCount(),
                cameraData.getAvgStayTime(),
                cameraData.getEventListJson());
    }

    /**
     * Dohvatanje poslednjeg heartbeat-a za dati serijski broj
     */
    public CameraHeartbeat getLastHeartbeat(String serialNumber) {
        String sql = "SELECT id, serial_number, timestamp, device_time, timezone_hours, upload_interval " +
                "FROM radio_bebakids.camera_heartbeats " +
                "WHERE serial_number = ? " +
                "ORDER BY timestamp DESC LIMIT 1";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, serialNumber);

        if (results.isEmpty()) {
            return null;
        }

        Map<String, Object> row = results.get(0);
        CameraHeartbeat heartbeat = new CameraHeartbeat();
        heartbeat.setId(((Number) row.get("id")).longValue());
        heartbeat.setSerialNumber((String) row.get("serial_number"));
        heartbeat.setTimestamp(((Timestamp) row.get("timestamp")).toLocalDateTime());
        heartbeat.setDeviceTime(((Number) row.get("device_time")).longValue());
        heartbeat.setTimezoneHours((Integer) row.get("timezone_hours"));
        heartbeat.setUploadInterval((Integer) row.get("upload_interval"));

        return heartbeat;
    }

    /**
     * Registrovanje nove kamere ako ne postoji
     */
    public void registerCameraIfNotExists(String serialNumber) {
        if (!existsCamera(serialNumber)) {
            String sql = "INSERT INTO radio_bebakids.cameras (serial_number, first_seen, last_seen, active) " +
                    "VALUES (?, ?, ?, 1)";
            LocalDateTime now = LocalDateTime.now();
            jdbcTemplate.update(sql, serialNumber, Timestamp.valueOf(now), Timestamp.valueOf(now));
        } else {
            // Ažuriranje vremena poslednjeg viđenja
            String sql = "UPDATE radio_bebakids.cameras SET last_seen = ? WHERE serial_number = ?";
            jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()), serialNumber);
        }
    }

    /**
     * Dohvatanje podataka o kameri
     */
    public Map<String, Object> getCameraInfo(String serialNumber) {
        String sql = "SELECT id, serial_number, first_seen, last_seen, active, location, description " +
                "FROM radio_bebakids.cameras WHERE serial_number = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, serialNumber);

        return results.isEmpty() ? null : results.get(0);
    }
}