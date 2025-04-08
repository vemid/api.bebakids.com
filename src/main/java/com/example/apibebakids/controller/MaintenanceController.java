package com.example.apibebakids.controller;

import com.example.apibebakids.service.LogCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final LogCleanupService logCleanupService;

    @Autowired
    public MaintenanceController(LogCleanupService logCleanupService) {
        this.logCleanupService = logCleanupService;
    }

    @PostMapping("/cleanup-logs")
    public ResponseEntity<Map<String, Object>> cleanupLogs(
            @RequestParam(defaultValue = "5") int minutes) {

        logCleanupService.cleanupOldLogsNow(minutes);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pokrenuto čišćenje log fajlova starijih od " + minutes + " minuta");

        return ResponseEntity.ok(response);
    }
}