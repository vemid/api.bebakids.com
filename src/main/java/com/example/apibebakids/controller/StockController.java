package com.example.apibebakids.controller;


import com.example.apibebakids.model.StockResponse;
import com.example.apibebakids.service.StockService;
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
@Tag(name = "Stock API", description = "API za dobavljanje informacija o zalihama")
public class StockController {



    @Autowired
    private StockService stockService;

    @GetMapping("/stockBySku")
    @Operation(
            summary = "Dobavlja informacije o zalihama za određeni SKU",
            description = "Vraća informacije o zalihama na osnovu SKU-a.",
            parameters = {
                    @Parameter(
                            name = "sku",
                            description = "SKU proizvoda",
                            required = true,
                            example = "3241OZ0D22A01-DUKS Ž NIKI"
                    ),
                    @Parameter(
                            name = "storeId",
                            description = "Predstavlja jedan objekat ",
                            required = false,
                            example = "03"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uspešno vraćene informacije o zalihama",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StockResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Došlo je do greške")
    })
    public ResponseEntity<?> getStockBySku(@RequestParam String system,@RequestParam String sku,@RequestParam(required = false) String storeId) {



        try {
            StockResponse stockResponse = stockService.getStockBySku(sku,system,storeId);
            return ResponseEntity.ok(stockResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/stockByStore")
    @Operation(
            summary = "Dobavlja informacije o zalihama za određeni SKU",
            description = "Vraća informacije o zalihama na osnovu SKU-a.",
            parameters = {
                    @Parameter(
                            name = "storeId",
                            description = "Store ID",
                            required = true,
                            example = "03"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uspešno vraćene informacije o zalihama",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StockResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Došlo je do greške")
    })
    public ResponseEntity<?> getStockByStore(@RequestParam String system,@RequestParam String storeId) {
        try {
            StockResponse stockResponse = stockService.getStockByStore(system,storeId);
            return ResponseEntity.ok(stockResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}