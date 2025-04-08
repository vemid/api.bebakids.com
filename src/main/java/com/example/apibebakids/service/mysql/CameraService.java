package com.example.apibebakids.service.mysql;

import com.example.apibebakids.model.mysql.CameraHeartbeat;
import com.example.apibebakids.model.mysql.CameraData;
import com.example.apibebakids.repository.mysql.CameraRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CameraService {
    private static final Logger logger = LoggerFactory.getLogger(CameraService.class);

    private final CameraRepository cameraRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public CameraService(CameraRepository cameraRepository, ObjectMapper objectMapper) {
        this.cameraRepository = cameraRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Obrađuje heartbeat signal od kamere i čuva podatke
     */
    public void processHeartbeat(String serialNumber, long deviceTime, int timezoneHours, int uploadInterval) {
        try {
            // Registruj kameru ako ne postoji
            cameraRepository.registerCameraIfNotExists(serialNumber);

            // Kreiraj i sačuvaj heartbeat
            ZoneId belgradeZone = ZoneId.of("Europe/Belgrade");
            ZonedDateTime nowBelgrade = ZonedDateTime.now(belgradeZone);
            long currentTime = nowBelgrade.toEpochSecond();
            LocalDateTime ldCurrentTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(currentTime), belgradeZone);
            CameraHeartbeat heartbeat = new CameraHeartbeat(
                    serialNumber, ldCurrentTime, deviceTime, timezoneHours, uploadInterval);

            cameraRepository.saveHeartbeat(heartbeat);

            logger.info("Heartbeat uspešno sačuvan za kameru: {}", serialNumber);
        } catch (Exception e) {
            logger.error("Greška pri čuvanju heartbeat-a za kameru {}: {}", serialNumber, e.getMessage(), e);
        }
    }

    /**
     * Obrađuje podatke pristigle od kamere
     */
    public void processCameraData(Map<String, Object> uploadData) {
        try {
            String serialNumber = uploadData.containsKey("sn") ? uploadData.get("sn").toString() : "nepoznat";

            // Registruj kameru ako ne postoji
            cameraRepository.registerCameraIfNotExists(serialNumber);

            // Preuzmi vrednosti iz uploadData mape
            Long time = getLongValue(uploadData, "time");
            Long startTime = getLongValue(uploadData, "startTime");
            Long endTime = getLongValue(uploadData, "endTime");
            Integer inCount = getIntValue(uploadData, "in");
            Integer outCount = getIntValue(uploadData, "out");
            Integer passByCount = getIntValue(uploadData, "passby");
            Integer turnBackCount = getIntValue(uploadData, "turnback");
            Integer avgStayTime = getIntValue(uploadData, "avgStayTime");

            // Konvertuj eventList u JSON string ako postoji
            String eventListJson = null;
            if (uploadData.containsKey("eventList") && uploadData.get("eventList") instanceof List) {
                try {
                    eventListJson = objectMapper.writeValueAsString(uploadData.get("eventList"));
                } catch (JsonProcessingException e) {
                    logger.error("Greška pri konverziji eventList u JSON: {}", e.getMessage());
                }
            }

            // Pretvori Unix vreme u LocalDateTime
            ZoneId belgradeZone = ZoneId.of("Europe/Belgrade");
            ZonedDateTime nowBelgrade = ZonedDateTime.now(belgradeZone);
            long currentTime = nowBelgrade.toEpochSecond();
            LocalDateTime ldCurrentTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(currentTime), belgradeZone);
//            if (time != null) {
//                ldCurrentTime = LocalDateTime.ofInstant(
//                        Instant.ofEpochSecond(ldCurrentTime), belgradeZone);
//            }

            // Kreiraj i sačuvaj podatke kamere
            CameraData cameraData = new CameraData(
                    serialNumber, ldCurrentTime, startTime, endTime,
                    inCount, outCount, passByCount, turnBackCount,
                    avgStayTime, eventListJson);

            cameraRepository.saveCameraData(cameraData);

            logger.info("Podaci kamere uspešno sačuvani za: {}", serialNumber);
        } catch (Exception e) {
            logger.error("Greška pri čuvanju podataka kamere: {}", e.getMessage(), e);
        }
    }

    /**
     * Pomoćna metoda za preuzimanje Long vrednosti iz mape
     */
    private Long getLongValue(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                try {
                    return Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Pomoćna metoda za preuzimanje Integer vrednosti iz mape
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}