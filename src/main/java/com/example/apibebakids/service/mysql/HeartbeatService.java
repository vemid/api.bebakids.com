package com.example.apibebakids.service.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.apibebakids.model.mysql.DeviceHeartbeat;
import com.example.apibebakids.repository.mysql.HeartbeatRepository;
import java.time.LocalDateTime;

@Service
public class HeartbeatService {
    @Autowired
    private HeartbeatRepository heartbeatRepository;

    @Transactional
    public void recordHeartbeat(String deviceId) {
        LocalDateTime now = LocalDateTime.now();

        if (heartbeatRepository.existsHeartbeatForToday(deviceId)) {
            // Update existing record
            heartbeatRepository.updateHeartbeat(deviceId, now);
        } else {
            // Create new record
            DeviceHeartbeat heartbeat = new DeviceHeartbeat(deviceId);
            heartbeat.setTimestamp(now);
            heartbeatRepository.saveHeartbeat(heartbeat);
        }
    }
}