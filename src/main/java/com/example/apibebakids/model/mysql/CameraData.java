package com.example.apibebakids.model.mysql;

import java.time.LocalDateTime;

public class CameraData {
    private Long id;
    private String serialNumber;
    private LocalDateTime timestamp;
    private Long startTime;
    private Long endTime;
    private Integer inCount;
    private Integer outCount;
    private Integer passByCount;
    private Integer turnBackCount;
    private Integer avgStayTime;
    private String eventListJson;

    public CameraData() {
    }

    public CameraData(String serialNumber, LocalDateTime timestamp, Long startTime, Long endTime,
                      Integer inCount, Integer outCount, Integer passByCount, Integer turnBackCount,
                      Integer avgStayTime, String eventListJson) {
        this.serialNumber = serialNumber;
        this.timestamp = timestamp;
        this.startTime = startTime;
        this.endTime = endTime;
        this.inCount = inCount;
        this.outCount = outCount;
        this.passByCount = passByCount;
        this.turnBackCount = turnBackCount;
        this.avgStayTime = avgStayTime;
        this.eventListJson = eventListJson;
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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getInCount() {
        return inCount;
    }

    public void setInCount(Integer inCount) {
        this.inCount = inCount;
    }

    public Integer getOutCount() {
        return outCount;
    }

    public void setOutCount(Integer outCount) {
        this.outCount = outCount;
    }

    public Integer getPassByCount() {
        return passByCount;
    }

    public void setPassByCount(Integer passByCount) {
        this.passByCount = passByCount;
    }

    public Integer getTurnBackCount() {
        return turnBackCount;
    }

    public void setTurnBackCount(Integer turnBackCount) {
        this.turnBackCount = turnBackCount;
    }

    public Integer getAvgStayTime() {
        return avgStayTime;
    }

    public void setAvgStayTime(Integer avgStayTime) {
        this.avgStayTime = avgStayTime;
    }

    public String getEventListJson() {
        return eventListJson;
    }

    public void setEventListJson(String eventListJson) {
        this.eventListJson = eventListJson;
    }
}