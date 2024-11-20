package com.example.apibebakids.model.mysql;

public class ProductionWorker {
    private Long id;
    private String name;
    private String employeeCode;
    // Assuming other necessary fields

    public ProductionWorker(Long id, String name, String employeeCode) {
        this.id = id;
        this.name = name;
        this.employeeCode = employeeCode;
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

    // Add other getters and setters as needed
}
