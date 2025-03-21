package com.example.apibebakids.controller;

import com.example.apibebakids.service.PrenosnicaMaloprodajeService;
import com.example.apibebakids.service.OtpremnicaUMaloprodajuService;
import com.example.apibebakids.service.PovratnicaMaloprodajeService;
import com.example.apibebakids.service.NalogZaOtpremuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mis/documents")
@Tag(name = "MIS Documents", description = "API for managing MIS documents")
public class MisDocumentsController {

    @Autowired
    private PrenosnicaMaloprodajeService prenosnicaMaloprodajeService;

    @Autowired
    private OtpremnicaUMaloprodajuService otpremnicaUMaloprodajuService;

    @Autowired
    private PovratnicaMaloprodajeService povratnicaMaloprodajeService;


    @Autowired
    private NalogZaOtpremuService nalogZaOtpremuService;

    @Value("${soap.service.urlWATCH}")
    private String soapServiceUrlWatch;

    @Value("${soap.service.urlBK}")
    private String soapServiceUrlBK;

    @Value("${soap.service.urlCF}")
    private String soapServiceUrlCF;

    @Value("${soap.service.urlBKBIH}")
    private String soapServiceUrlBKBIH;

    @Value("${soap.service.urlBKMNE}")
    private String soapServiceUrlBKMNE;

    @Value("${soap.service.username}")
    private String username;

    @Value("${soap.service.password}")
    private String password;

    private String getUrl(String system) {
        switch (system.toLowerCase()) {
            case "bebakids":
                return soapServiceUrlBK;
            case "watch":
                return soapServiceUrlWatch;
            case "geox":
                return soapServiceUrlCF;
            case "bebakidsbih":
                return soapServiceUrlBKBIH;
            case "bebakidsmne":
                return soapServiceUrlBKMNE;
            default:
                throw new IllegalArgumentException("Invalid system: " + system);
        }
    }

    @PostMapping("/dodajPrenosnicuMaloprodaje")
    @Operation(summary = "Add a new retail transfer document",
            description = "Creates a new retail transfer document (Prenosnica Maloprodaje) in the MIS system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created the document",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<?> dodajPrenosnicuMaloprodaje(
            @Parameter(description = "Code of the source store", required = true)
            @RequestParam("storeFrom") String storeFrom,

            @Parameter(description = "Code of the destination store", required = true)
            @RequestParam("storeTo") String storeTo,

            @Parameter(description = "System identifier (bebakids, watch, or geox)", required = true)
            @RequestParam("system") String system,

            @Parameter(description = "JSON array of items to transfer", required = true,
                    example = "[{\"sku\":\"12345\",\"size\":\"M\",\"qty\":\"10\"}]")
            @RequestParam("items") String items,

            @Parameter(description = "Pricelist identifier", required = true)
            @RequestParam("pricelist") String pricelist,

            @Parameter(description = "Username of the person creating the document", required = true)
            @RequestParam("logname") String logname) {

        String baseUrl = getUrl(system);
        String url = baseUrl+"PrenosServisPort?wsdl";
        return prenosnicaMaloprodajeService.processPrenosnicaMaloprodaje(storeFrom, storeTo, system, items, pricelist, logname, username, password, url);
    }

    @PostMapping("/dodajOtpremnicuUMaloprodaju")
    @Operation(summary = "Add a new retail shipment document",
            description = "Creates a new retail shipment document (OtpremnicaUMaloprodaju) in the MIS system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created the document",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<?> dodajOtpremnicuUMaloprodaju(
            @RequestParam("storeCode") String storeCode,
            @RequestParam("retailStoreCode") String retailStoreCode,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("system") String system,
            @RequestParam("items") String items,
            @RequestParam("pricelist") String pricelist,
            @RequestParam("logname") String logname) {

        String baseUrl = getUrl(system);
        String url = baseUrl+"OptremnicaServisPort?wsdl";
        return otpremnicaUMaloprodajuService.processOtpremnicaUMaloprodaju(storeCode, retailStoreCode, note, system, items, pricelist, logname, username, password, url);
    }


    @PostMapping("/dodajPovratnicuMaloprodaje")
    @Operation(summary = "Add a new retail return document",
            description = "Creates a new retail return document (Povratnica Maloprodaje) in the MIS system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created the document",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<?> dodajPovratnicuMaloprodaje(
            @Parameter(description = "Code of the warehouse", required = true)
            @RequestParam("warehouseCode") String warehouseCode,

            @Parameter(description = "Code of the retail store", required = true)
            @RequestParam("retailStoreCode") String retailStoreCode,

            @Parameter(description = "System identifier (bebakids, watch, or geox)", required = true)
            @RequestParam("system") String system,

            @Parameter(description = "JSON array of items to return", required = true,
                    example = "[{\"sku\":\"12345\",\"size\":\"M\",\"qty\":\"10\"}]")
            @RequestParam("items") String items,

            @Parameter(description = "Pricelist identifier", required = true)
            @RequestParam("pricelist") String pricelist,

            @Parameter(description = "Username of the person creating the document", required = true)
            @RequestParam("logname") String logname,

            @Parameter(description = "Reason for return", required = false)
            @RequestParam(value ="reasonForReturn", required = false) String reasonForReturn) {

        String baseUrl = getUrl(system);
        String url = baseUrl + "PovratnicaServisPort?wsdl";
        return povratnicaMaloprodajeService.processPovratnicaMaloprodaje(warehouseCode, retailStoreCode, system, items, pricelist, logname, reasonForReturn, username, password, url);
    }

    @PostMapping("/dodajNalogZaOtpremu")
    @Operation(summary = "Add a new shipping order document",
            description = "Creates a new shipping order document (Nalog Za Otpremu) in the MIS system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created the document",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<?> dodajNalogZaOtpremu(
            @Parameter(description = "Code of the warehouse", required = true)
            @RequestParam("warehouseCode") String warehouseCode,

            @Parameter(description = "Shipping destination", required = true)
            @RequestParam("destination") String destination,

            @Parameter(description = "System identifier (bebakids, watch, or geox)", required = true)
            @RequestParam("system") String system,

            @Parameter(description = "JSON array of items to ship", required = true,
                    example = "[{\"sku\":\"12345\",\"size\":\"M\",\"qty\":\"10\"}]")
            @RequestParam("items") String items,

            @Parameter(description = "Pricelist identifier", required = true)
            @RequestParam("pricelist") String pricelist,

            @Parameter(description = "Username of the person creating the document", required = true)
            @RequestParam("logname") String logname,

            @Parameter(description = "Additional notes for the shipment", required = false)
            @RequestParam(value = "note", required = false) String note) {

        String baseUrl = getUrl(system);
        String url = baseUrl + "DodajNalogZaIzdavanjePort?wsdl";
        return nalogZaOtpremuService.processNalogZaOtpremu(warehouseCode, system, items, pricelist, logname, destination, note, username, password, url);
    }
}