package com.example.apibebakids.controller;

import com.example.apibebakids.model.mysql.LocalApiResponse;
import com.example.apibebakids.service.mysql.HeartbeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/radio/heartbeat")
@Tag(name = "Device Heartbeat API", description = "API for managing device heartbeats")
public class HeartbeatController {

    @Autowired
    private HeartbeatService heartbeatService;

    @Operation(summary = "Record device heartbeat", description = "Records a heartbeat from a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Heartbeat recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid device ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<LocalApiResponse<String>> recordHeartbeat(
            @Parameter(description = "Device ID", required = true)
            @RequestParam String deviceId) {
        try {
            heartbeatService.recordHeartbeat(deviceId);
            return ResponseEntity.ok(
                    new LocalApiResponse<>(true, "Heartbeat recorded successfully", "Success", null)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LocalApiResponse<>(false, "Failed to record heartbeat", null,
                            Collections.singletonList(e.getMessage())));
        }
    }
}