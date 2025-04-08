package com.example.apibebakids.model.mysql;

import java.time.LocalDateTime;

public class CameraHeartbeat {
    private Long id;
    private String serialNumber;
    private LocalDateTime timestamp;
    private Long deviceTime;
    private Integer timezoneHours;
    private Integer uploadInterval;

    public CameraHeartbeat() {
    }

    public CameraHeartbeat(String serialNumber, LocalDateTime timestamp, Long deviceTime, Integer timezoneHours, Integer uploadInterval) {
        this.serialNumber = serialNumber;
        this.timestamp = timestamp;
        this.deviceTime = deviceTime;
        this.timezoneHours = timezoneHours;
        this.uploadInterval = uploadInterval;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(Long deviceTime) {
        this.deviceTime = deviceTime;
    }

    public Integer getTimezoneHours() {
        return timezoneHours;
    }

    public void setTimezoneHours(Integer timezoneHours) {
        this.timezoneHours = timezoneHours;
    }

    public Integer getUploadInterval() {
        return uploadInterval;
    }

    public void setUploadInterval(Integer uploadInterval) {
        this.uploadInterval = uploadInterval;
    }
}