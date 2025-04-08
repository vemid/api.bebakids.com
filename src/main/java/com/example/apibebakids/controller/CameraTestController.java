package com.example.apibebakids.controller;

import com.example.apibebakids.service.mysql.CameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/servis/api/camera")
public class CameraTestController {

    private static final Logger logger = LoggerFactory.getLogger(CameraTestController.class);
    private final Path uploadDir = Paths.get("camera-uploads");
    private final CameraService cameraService;

    @Autowired
    public CameraTestController(CameraService cameraService) {
        this.cameraService = cameraService;

//        // Kreiraj direktorijum za slike ako ne postoji
//        try {
//            if (!Files.exists(uploadDir)) {
//                Files.createDirectories(uploadDir);
//                logger.info("Kreiran direktorijum za slike: {}", uploadDir.toAbsolutePath());
//            }
//        } catch (IOException e) {
//            logger.error("Greška pri kreiranju direktorijuma za slike", e);
//        }
    }

    /**
     * Metoda za prihvatanje podataka o statistici sa kamere
     * Format ulaznih podataka obuhvata statistiku o prolasku osoba i listu događaja.
     */
    @PostMapping("/dataUpload")
    public ResponseEntity<Map<String, Object>> dataUpload(@RequestBody Map<String, Object> uploadData) {
        logger.info("==== NOVI ZAHTEV ZA UPLOAD PODATAKA ====");
        logger.info("Vreme: {}", LocalDateTime.now());
        logger.info("Primljeni podaci: {}", uploadData);

        // Ekstrahujemo serijski broj iz zahteva
        String serialNumber = uploadData.containsKey("sn") ? uploadData.get("sn").toString() : "nepoznat";

        // Sačuvaj podatke u bazi
        cameraService.processCameraData(uploadData);

        // Kreiramo odgovor prema traženom formatu
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("sn", serialNumber);
        dataResponse.put("time", System.currentTimeMillis() / 1000); // trenutno UNIX vreme

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("msg", "success");
        response.put("data", dataResponse);

        logger.info("Odgovor na dataUpload: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/heartBeat")
    public ResponseEntity<Map<String, Object>> heartBeat(@RequestBody Map<String, Object> heartbeatData) {
        logger.info("==== NOVI HEARTBEAT ====");
        logger.info("Vreme primanja zahteva: {}", LocalDateTime.now());
        logger.info("Primljeni podaci: {}", heartbeatData);

        // Ekstrahujemo serijski broj iz zahteva
        String serialNumber = heartbeatData.containsKey("sn") ? heartbeatData.get("sn").toString() : "nepoznat";
        Long requestTime = 0L;

        try {
            if (heartbeatData.containsKey("time")) {
                if (heartbeatData.get("time") instanceof Number) {
                    requestTime = ((Number) heartbeatData.get("time")).longValue();
                } else {
                    requestTime = Long.parseLong(heartbeatData.get("time").toString());
                }
            }
        } catch (Exception e) {
            logger.error("Greška pri parsiranju vremena: {}", e.getMessage());
        }

        logger.info("Serijski broj kamere: {}", serialNumber);
        logger.info("Vreme iz zahteva kamere: {}", requestTime);

        if (requestTime > 0) {
            // Formatiramo vreme iz zahteva za lakše razumevanje
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldtRequestTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(requestTime),
                    ZoneId.systemDefault());
            logger.info("Vreme iz zahteva kamere (formatirano): {}", ldtRequestTime.format(formatter));
        }

        // Dobijanje tačnog trenutnog vremena
        ZoneId belgradeZone = ZoneId.of("Europe/Belgrade");
        ZonedDateTime nowBelgrade = ZonedDateTime.now(belgradeZone);
        long currentTime = nowBelgrade.toEpochSecond();

        // Vremenska zona za Beograd (CET/CEST)
        ZoneOffset offset = nowBelgrade.getOffset();
        int timezoneHours = offset.getTotalSeconds() / 3600;

        // Formatirano trenutno vreme za lakše debagovanje
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logger.info("Trenutno vreme (Beograd): {}", nowBelgrade.format(formatter));
        logger.info("Trenutno vreme (Unix timestamp): {}", currentTime);
        logger.info("Vremenska zona (Beograd): GMT{}{}",
                timezoneHours >= 0 ? "+" : "",
                timezoneHours);

        // Kreiranje odgovora sa tačnim vremenom
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("sn", serialNumber);

        // Računanje perioda za prikupljanje podataka (poslednja 3 minuta)
        long startTime = currentTime - (50 * 60);
        //responseData.put("dataStartTime", startTime);
        //responseData.put("dataEndTime", currentTime);
        responseData.put("timezone", timezoneHours);
        //responseData.put("dataMode", "Add");
        //responseData.put("uploadInterval", 0); // Upload na svaka 3 minuta
        responseData.put("time", currentTime);

        // Sačuvaj heartbeat u bazi
        cameraService.processHeartbeat(serialNumber, requestTime, timezoneHours, 2);

        // Logujemo formatirane verzije vremenskih oznaka za lakše debagovanje
        LocalDateTime ldtStartTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(startTime), belgradeZone);
        LocalDateTime ldtEndTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(currentTime), belgradeZone);

        logger.info("Vreme početka perioda: {} ({})",
                ldtStartTime.format(formatter), startTime);
        logger.info("Vreme kraja perioda: {} ({})",
                ldtEndTime.format(formatter), currentTime);

        // Kreiranje završnog odgovora
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("msg", "success");
        response.put("data", responseData);

        logger.info("Odgovor na heartbeat: {}", response);

        return ResponseEntity.ok(response);
    }
}