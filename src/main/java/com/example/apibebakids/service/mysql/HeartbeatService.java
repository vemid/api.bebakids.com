package com.example.apibebakids.service.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.apibebakids.model.mysql.DeviceHeartbeat;
import com.example.apibebakids.repository.mysql.HeartbeatRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class HeartbeatService {
    @Autowired
    private HeartbeatRepository heartbeatRepository;

    @Transactional
    public void recordHeartbeat(String deviceId) {
        LocalDateTime now = LocalDateTime.now();

        if (heartbeatRepository.existsHeartbeatForToday(deviceId)) {
            heartbeatRepository.updateHeartbeat(deviceId, now);
        } else {
            DeviceHeartbeat heartbeat = new DeviceHeartbeat(deviceId);
            heartbeat.setTimestamp(now);
            heartbeatRepository.saveHeartbeat(heartbeat);
        }
    }

    public List<Map<String, Object>> getDevices(String station) {
        return heartbeatRepository.getDevices(station);
    }
}