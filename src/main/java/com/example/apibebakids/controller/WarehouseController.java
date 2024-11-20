package com.example.apibebakids.controller;

import com.example.apibebakids.model.WarehouseResponse;
import com.example.apibebakids.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouses")
@Tag(name = "Warehouses API", description = "API za dobavljanje informacija o skladištima")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    @Operation(
            summary = "Dobavlja listu skladišta",
            description = "Vraća informacije o skladištima na osnovu navedenog sistema.",
            parameters = {
                    @Parameter(
                            name = "system",
                            description = "Naziv sistema (bebakids, watch, geox,bebakidsbih)",
                            required = true,
                            example = "bebakids"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uspešno vraćena lista skladišta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WarehouseResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Neispravan zahtev - neispravan sistem"),
            @ApiResponse(responseCode = "500", description = "Interna greška servera")
    })
    public ResponseEntity<WarehouseResponse> getWarehouses(@RequestParam String system) {
        try {
            WarehouseResponse response = warehouseService.getWarehouses(system);
            if (!response.isResponseResult()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            WarehouseResponse errorResponse = new WarehouseResponse();
            errorResponse.setResponseResult(false);
            errorResponse.setErrorMessage("Greška: " + e.getMessage());
            errorResponse.setRespResultCount(0);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}