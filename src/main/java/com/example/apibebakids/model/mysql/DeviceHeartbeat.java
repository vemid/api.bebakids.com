package com.example.apibebakids.model.mysql;

import java.time.LocalDateTime;

public class DeviceHeartbeat {
    private Long id;
    private String deviceId;
    private LocalDateTime timestamp;

    // Constructors
    public DeviceHeartbeat() {}

    public DeviceHeartbeat(String deviceId) {
        this.deviceId = deviceId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}