package com.example.apibebakids.model.mysql;

import org.springframework.cglib.core.Local;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class ProductionWorkerSyncCheckins {
    private Long id;
    private String employeeCode;
    private String name;
    private LocalDate checkinDate;
    private LocalTime checkinTime;
    private LocalTime checkinEndTime;
    // Assuming other necessary fields

    public ProductionWorkerSyncCheckins(Long id, String employeeCode, String name,
                                        LocalDate checkinDate, LocalTime checkinTime,
                                        LocalTime checkinEndTime) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalTime getCheckinEndTime() {
        return checkinEndTime;
    }

    public void setCheckinEndTime(LocalTime checkinEndTime) {
        this.checkinEndTime = checkinEndTime;
    }

    public LocalTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalTime checkinTime) {
        this.checkinTime = checkinTime;
    }


}
