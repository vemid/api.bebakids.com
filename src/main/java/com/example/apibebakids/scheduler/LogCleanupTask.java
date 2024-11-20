package com.example.apibebakids.scheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class LogCleanupTask {

    // Inject log directory path from application.properties
    @Value("${log.file.path}")
    private String logDirectory;

    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
    public void cleanOldLogs() {
        File logDir = new File(logDirectory);
        if (logDir.exists() && logDir.isDirectory()) {
            File[] files = logDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        Path filePath = file.toPath();
                        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                        Instant fileCreationTime = attrs.creationTime().toInstant();
                        if (fileCreationTime.isBefore(Instant.now().minus(7, ChronoUnit.DAYS))) {
                            file.delete();
                            System.out.println("Deleted log file: " + file.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle the exception accordingly
                    }
                }
            }
        }
    }
}