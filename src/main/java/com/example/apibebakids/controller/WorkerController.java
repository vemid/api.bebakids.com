package com.example.apibebakids.controller;

import com.example.apibebakids.model.mysql.ProductionWorker;
import com.example.apibebakids.model.mysql.LocalApiResponse;
import com.example.apibebakids.model.mysql.ProductionWorkerCheckin;
import com.example.apibebakids.service.mysql.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;


import java.util.List;

@RestController
@RequestMapping("/api/workers")
@Tag(name = "Worker API", description = "API for managing workers and their check-ins")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @Operation(summary = "Get all workers", description = "Retrieves a list of all workers based on location.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid or missing location ID", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @GetMapping("/getWorkersByLocation")
    public ResponseEntity<LocalApiResponse<List<ProductionWorker>>> getAllWorkers(
            @Parameter(description = "ID of the location to filter workers by", required = true)
            @RequestParam("locationId") String locationId) {
        try {
            List<ProductionWorker> workers = workerService.getAllWorkersByLocation(locationId);
            LocalApiResponse<List<ProductionWorker>> response = new LocalApiResponse<>(true, "Successfully retrieved list", workers, null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            LocalApiResponse<List<ProductionWorker>> response = new LocalApiResponse<>(false, "Invalid location ID: " + locationId, null, Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            LocalApiResponse<List<ProductionWorker>> response = new LocalApiResponse<>(false, "An unexpected error occurred", null, Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Record worker check-in", description = "Saves a new check-in record for a worker.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-in recorded successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })

    @PostMapping("/saveWorkersCheckinsByLocation")
    public ResponseEntity<LocalApiResponse<String>> recordWorkerCheckins(@RequestBody CheckinRequest checkinRequest) {
        try {
            workerService.recordWorkerCheckins(checkinRequest.getCheckins());
            LocalApiResponse<String> response = new LocalApiResponse<>(true, "Check-ins recorded successfully", "Operation successful", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            LocalApiResponse<String> response = new LocalApiResponse<>(false, "Error recording check-ins", null, Collections.singletonList(e.getMessage()));
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            LocalApiResponse<String> response = new LocalApiResponse<>(false, "An unexpected error occurred", null, Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    static class CheckinRequest {
        private String locationId;
        private List<ProductionWorkerCheckin> checkins;

        // Getters and Setters
        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        public List<ProductionWorkerCheckin> getCheckins() {
            return checkins;
        }

        public void setCheckins(List<ProductionWorkerCheckin> checkins) {
            this.checkins = checkins;
        }
    }
}
