package com.example.apibebakids.controller;

import com.example.apibebakids.model.BarcodeResponse;
import com.example.apibebakids.service.DatabaseService;
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
@RequestMapping("/api")
@Tag(name = "Barcodes API", description = "API za dobavljanje barkodova iz baze podataka")
public class BarcodeController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/barcodes")
    @Operation(
            summary = "Dobavlja listu barkodova",
            description = "Vraća barkodove na osnovu sistema (bebakids, watch, geox).",
            parameters = {
                    @Parameter(
                            name = "system",
                            description = "Naziv sistema (bebakids, watch, geox, bebakidsbih)",
                            required = true,
                            example = "bebakids"
                    ),
                    @Parameter(
                            name = "barkod",
                            description = "Predstavlja jedan objekat barkoda",
                            required = false,
                            example = "5392A20330B02"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uspešno vraćena lista barkodova",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BarcodeResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Neispravan zahtev"),
            @ApiResponse(responseCode = "500", description = "Interna greška servera")
    })
    public ResponseEntity<BarcodeResponse> getBarcodes(@RequestParam String system, @RequestParam(required = false) String barkod) {
        if (!isValidSystem(system)) {
            BarcodeResponse errorResponse = new BarcodeResponse();
            errorResponse.setResponseResult(false);
            errorResponse.setErrorMessage("Neispravan sistem: " + system);
            errorResponse.setRespResultCount(0);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            BarcodeResponse response;
            if (barkod != null && !barkod.isEmpty()) {
                response = databaseService.getSkuByBarcode(system, barkod);
            } else {
                response = databaseService.getBarcodes(system);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BarcodeResponse errorResponse = new BarcodeResponse();
            errorResponse.setResponseResult(false);
            errorResponse.setErrorMessage("Greška: " + e.getMessage());
            errorResponse.setRespResultCount(0);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    private boolean isValidSystem(String system) {
        return "bebakids".equalsIgnoreCase(system) ||
                "watch".equalsIgnoreCase(system) ||
                "bebakidsbih".equalsIgnoreCase(system) ||
                "geox".equalsIgnoreCase(system);
    }

    @GetMapping("/barcodesById")
    @Operation(
            summary = "Dobavlja listu barkodova",
            description = "Vraća barkodove na osnovu sistema (bebakids, watch, geox,bebakidsbih).",
            parameters = {
                    @Parameter(
                            name = "system",
                            description = "Naziv sistema (bebakids, watch, geox,bebakidsbih)",
                            required = true,
                            example = "bebakids"
                    ),
                    @Parameter(
                            name = "barkod",
                            description = "Predstavlja jedan objekat barkoda",
                            required = false,
                            example = "5392A20330B02"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uspešno vraćena lista barkodova",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BarcodeResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Neispravan zahtev"),
            @ApiResponse(responseCode = "500", description = "Interna greška servera")
    })
    public ResponseEntity<BarcodeResponse> getBarcodesById(@RequestParam String system, @RequestParam Integer id) {
        if (!isValidSystem(system)) {
            BarcodeResponse errorResponse = new BarcodeResponse();
            errorResponse.setResponseResult(false);
            errorResponse.setErrorMessage("Neispravan sistem: " + system);
            errorResponse.setRespResultCount(0);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            BarcodeResponse response;
            response = databaseService.getBarcodesById(system,id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BarcodeResponse errorResponse = new BarcodeResponse();
            errorResponse.setResponseResult(false);
            errorResponse.setErrorMessage("Greška: " + e.getMessage());
            errorResponse.setRespResultCount(0);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}