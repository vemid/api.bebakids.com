package com.example.apibebakids.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@Service
public class LogCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(LogCleanupService.class);

    @Value("${logs.cleanup.minutes:5}")
    private int retentionMinutes;

    @Value("${logs.directory:C:/projects/logs}")
    private String logDirectory;

    /**
     * Periodično čišćenje starih log fajlova
     * Izvršava se svakih 5 minuta (300000 ms)
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupOldLogs() {
        logger.info("Pokretanje čišćenja starih log fajlova u direktorijumu: {}", logDirectory);

        Path logsPath = Paths.get(logDirectory);
        if (!Files.exists(logsPath)) {
            logger.warn("Direktorijum za logove ne postoji: {}", logDirectory);
            return;
        }

        Instant cutoffTime = Instant.now().minus(Duration.ofMinutes(retentionMinutes));
        int deletedFiles = 0;

        try (Stream<Path> paths = Files.list(logsPath)) {
            for (Path path : paths.toList()) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                try {
                    Instant lastModified = Files.getLastModifiedTime(path).toInstant();

                    if (lastModified.isBefore(cutoffTime)) {
                        Files.delete(path);
                        deletedFiles++;
                        logger.debug("Obrisan log fajl: {}", path.getFileName());
                    }
                } catch (IOException e) {
                    logger.error("Greška pri brisanju log fajla {}: {}", path.getFileName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Greška pri listanju log fajlova: {}", e.getMessage());
        }

        logger.info("Čišćenje završeno. Obrisano {} log fajlova starijih od {} minuta.", deletedFiles, retentionMinutes);
    }

    // Dodatna metoda koja se može pozvati po potrebi, van zakazanog izvršavanja
    public int cleanupOldLogsNow(int minutes) {
        this.retentionMinutes = minutes;
        int previousRetention = retentionMinutes;

        try {
            cleanupOldLogs();
            return previousRetention;
        } finally {
            // Vraćamo prethodnu vrednost
            this.retentionMinutes = previousRetention;
        }
    }
}