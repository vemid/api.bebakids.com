package com.example.apibebakids.controller;

import com.example.apibebakids.model.mysql.LocalApiResponse;
import com.example.apibebakids.service.mysql.HeartbeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/radio/heartbeat")
@Tag(name = "Device Heartbeat API", description = "API for managing device radios heartbeats")
public class HeartbeatController {

    @Autowired
    private HeartbeatService heartbeatService;

    @PostMapping
    @Operation(summary = "Record device heartbeat", description = "Records a heartbeat from a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Heartbeat recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid device ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @GetMapping("/devices")
    @Operation(
            summary = "Get devices by station",
            description = "Retrieves all devices for a specific station"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Devices retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                            "success": true,
                            "message": "Devices retrieved successfully",
                            "data": [
                                {
                                    "id": 1,
                                    "device_id": "device123",
                                    "shop_name": "Shop A",
                                    "active": 1,
                                    "station": "Station 1",
                                    "ip": "192.168.1.100"
                                },
                                {
                                    "id": 3,
                                    "device_id": "device789",
                                    "shop_name": "Shop C",
                                    "active": 1,
                                    "station": "Station 1",
                                    "ip": "192.168.1.102"
                                }
                            ],
                            "errors": null
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid station name"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LocalApiResponse<List<Map<String, Object>>>> getDevicesByStation(
            @Parameter(description = "Station name", required = true,
                    example = "Station 1")
            @RequestParam String stationName) {
        try {
            List<Map<String, Object>> devices = heartbeatService.getDevices(stationName);
            return ResponseEntity.ok(
                    new LocalApiResponse<>(
                            true,
                            "Devices retrieved successfully",
                            devices,
                            null
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LocalApiResponse<>(
                            false,
                            "Failed to retrieve devices",
                            null,
                            Collections.singletonList(e.getMessage())
                    ));
        }
    }
}