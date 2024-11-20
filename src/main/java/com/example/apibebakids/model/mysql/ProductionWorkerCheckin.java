package com.example.apibebakids.model.mysql;

import java.time.LocalDate;
import java.time.LocalTime;

public class ProductionWorkerCheckin {
    private Long id;
    private String workerId;
    private LocalDate checkinDate;
    private LocalTime checkinTime;
    private LocalTime checkinEndTime;
    // Assuming other necessary fields

    public ProductionWorkerCheckin(Long id, String workerId, LocalDate checkinDate, LocalTime checkinTime, LocalTime checkinEndTime) {
        this.id = id;
        this.workerId = workerId;
        this.checkinDate = checkinDate;
        this.checkinTime = checkinTime;
        this.checkinEndTime = checkinEndTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalTime getCheckinEndTime() {
        return checkinEndTime;
    }

    public void setCheckinEndTime(LocalTime checkinEndTime) {
        this.checkinEndTime = checkinEndTime;
    }

    // Add other getters and setters as needed
}
