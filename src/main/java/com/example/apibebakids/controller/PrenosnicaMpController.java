package com.example.apibebakids.controller;

import com.example.apibebakids.model.PrenosnicaMpDTO;
import com.example.apibebakids.model.PrenosnicaMpResponse;
import com.example.apibebakids.service.PrenosnicaMpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prenosnice-mp")
@Tag(name = "Prenosnice Maloprodaje API", description = "API za rad sa prenosnicama maloprodaje")
public class PrenosnicaMpController {

    @Autowired
    private PrenosnicaMpService prenosnicaMpService;

    @PostMapping("/dodaj")
    @Operation(
            summary = "Dodaje prenosnicu maloprodaje",
            description = "Kreira novu prenosnicu maloprodaje u sistemu",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Uspešno kreirana prenosnica",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PrenosnicaMpResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Neispravan zahtev"),
                    @ApiResponse(responseCode = "500", description = "Interna greška servera")
            }
    )
    public ResponseEntity<PrenosnicaMpResponse> dodajPrenosnicu(
            @Parameter(description = "Šifra objekta izlaza", required = true)
            @RequestParam("objekatIzlaza") String objekatIzlaza,

            @Parameter(description = "Šifra objekta ulaza", required = true)
            @RequestParam("objekatUlaza") String objekatUlaza,

            @Parameter(description = "Sistem (bebakids, watch, geox, bebakidsbih)", required = true)
            @RequestParam("system") String system,

            @Parameter(description = "JSON niz stavki za prenos", required = true,
                    example = "[{\"sifraRobe\":\"12345\",\"sifraObelezja\":\"M\",\"kolicina\":\"10\",\"stopaPoreza\":\"20\",\"brojPakovanja\":\"0\"}]")
            @RequestParam("items") String items,

            @Parameter(description = "Oznaka cenovnika", required = false)
            @RequestParam(value = "oznakaCenovnika", required = false) String oznakaCenovnika,

            @Parameter(description = "Korisničko ime osobe koja kreira dokument", required = true)
            @RequestParam("logname") String logname,

            @Parameter(description = "Vrsta knjiženja (1-SamoUlaz, 2-SamoIzlaz, 3-UlazIzlaz)", required = true)
            @RequestParam("vrstaKnjizenja") Integer vrstaKnjizenja,

            @Parameter(description = "Oznaka narudžbenice", required = false)
            @RequestParam(value = "oznakaNarudzbenice", required = false) String oznakaNarudzbenice) {

        return prenosnicaMpService.dodajPrenosnicu(
                objekatIzlaza, objekatUlaza, system, items, oznakaCenovnika,
                logname, vrstaKnjizenja, oznakaNarudzbenice);
    }

    @GetMapping("/lista")
    @Operation(
            summary = "Dobavlja listu prenosnica",
            description = "Vraća prenosnice na osnovu datuma.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Uspešno vraćena lista prenosnica",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PrenosnicaMpResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Neispravan zahtev"),
                    @ApiResponse(responseCode = "500", description = "Interna greška servera")
            }
    )
    public ResponseEntity<PrenosnicaMpResponse> getPrenosnice(
            @Parameter(description = "Datum od", required = false)
            @RequestParam(value = "datumOd", required = false) String datumOd,

            @Parameter(description = "Datum do", required = false)
            @RequestParam(value = "datumDo", required = false) String datumDo,

            @Parameter(description = "Sistem (bebakids, watch, geox, bebakidsbih)", required = true)
            @RequestParam("system") String system) {

        return prenosnicaMpService.getPrenosnice(datumOd, datumDo, system);
    }
}