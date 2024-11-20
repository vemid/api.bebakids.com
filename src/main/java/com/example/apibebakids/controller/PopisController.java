package com.example.apibebakids.controller;

import com.example.apibebakids.model.PopisItem;
import com.example.apibebakids.model.PopisRequest;
import com.example.apibebakids.service.PopisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Popis API", description = "API for managing inventory items")
public class PopisController {

    @Autowired
    private PopisService popisService;

    @Operation(
            summary = "Saves inventory items",
            description = "Creates a document for the specified retail store and saves the inventory items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items saved successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @PostMapping(value = "/createPopisRetailStore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> savePopisItems(
            @Parameter(description = "System to use (e.g., bebakids, watch, geox, bebakidsbih)", required = true)
            @RequestParam("system") String system,

            @Parameter(description = "Retail store associated with the document", required = true)
            @RequestParam("retailStore") String retailStore,

            @Parameter(description = "Retail store associated with the document", required = false)
            @RequestParam("note") String note,

            @Parameter(description = "Items JSON array", required = true)
            @RequestParam("items") String itemsJson
    ) {
        try {
            // Parse JSON string into a list of items
            List<PopisItem> items = parseItemsJson(itemsJson);
            PopisRequest popisRequest = new PopisRequest();
            popisRequest.setSystem(system);
            popisRequest.setRetailStore(retailStore);
            popisRequest.setNote(note);
            popisRequest.setItems(items);


            // Save items to database and create document
            String documentNumber = popisService.createDocumentAndSaveItems(popisRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Items saved successfully with document number: " + documentNumber);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    private List<PopisItem> parseItemsJson(String itemsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(itemsJson, new TypeReference<List<PopisItem>>(){});
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON format for items");
        }
    }
}
