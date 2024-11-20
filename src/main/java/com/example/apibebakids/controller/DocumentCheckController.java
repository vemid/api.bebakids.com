package com.example.apibebakids.controller;

import com.example.apibebakids.model.DocumentCheckResponse;
import com.example.apibebakids.service.DocumentCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@Tag(name = "Stock API", description = "API za dobavljanje informacija o zalihama")
public class DocumentCheckController {

    @Autowired
    private DocumentCheckService documentCheckService;

    @GetMapping("/documentCheck")
    @Operation(
            summary = "Dobavlja informacije o stavkama dokumenta",
            description = "Vraća informacije o stavkama dokumenta.",
            parameters = {
                    @Parameter(
                            name = "document",
                            description = "Document Number",
                            required = true,
                            example = "01/123456"
                    ),
                    @Parameter(
                            name = "documentType",
                            description = "OM WarehouseToStore",
                            required = true,
                            example = "WarehouseToStore"
                    ),
                    @Parameter(
                            name = "system",
                            description = "System",
                            required = true,
                            example = "bebakids"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uspešno vraćene informacije o stavkama dokumenta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DocumentCheckResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Došlo je do greške")
    })
    public ResponseEntity<?> getDocumentsData(@RequestParam String documentType,
                                              @RequestParam String document,
                                              @RequestParam String system) {
        try {
            // Manually decode the URL-encoded document parameter
            String decodedDocument = URLDecoder.decode(document, StandardCharsets.UTF_8.name());

            // Pass the decoded document to the service
            DocumentCheckResponse stockResponse = documentCheckService.getDocumentsData(documentType, decodedDocument, system);
            return ResponseEntity.ok(stockResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
