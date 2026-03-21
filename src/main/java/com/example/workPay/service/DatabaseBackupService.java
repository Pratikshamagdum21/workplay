package com.example.workPay.service;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

@Service
public class DatabaseBackupService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${backup.directory:backups}")
    private String backupDirectory;

    @Value("${backup.retention.count:7}")
    private int retentionCount;

    @Scheduled(cron = "${backup.cron:0 0 2 * * *}")
    public void performScheduledBackup() {
        logger.info("Starting scheduled database backup");
        boolean success = executeBackup();
        if (!success) {
            logger.warn("First backup attempt failed. Retrying once...");
            success = executeBackup();
            if (!success) {
                logger.error("Database backup failed after retry");
            }
        }
    }

    private boolean executeBackup() {
        try {
            String dbHost = getRequiredEnv("BACKUP_DB_HOST");
            String dbPort = getEnvOrDefault("BACKUP_DB_PORT", "5432");
            String dbName = getRequiredEnv("BACKUP_DB_NAME");
            String dbUser = getRequiredEnv("BACKUP_DB_USER");
            String dbPassword = getRequiredEnv("BACKUP_DB_PASSWORD");

            Path backupDir = Paths.get(backupDirectory);
            Files.createDirectories(backupDir);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String filename = String.format("backup_%s.sql", timestamp);
            Path backupFile = backupDir.resolve(filename);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "pg_dump",
                    "--host=" + dbHost,
                    "--port=" + dbPort,
                    "--username=" + dbUser,
                    "--dbname=" + dbName,
                    "--format=custom",
                    "--file=" + backupFile.toAbsolutePath()
            );

            processBuilder.environment().put("PGPASSWORD", dbPassword);
            processBuilder.redirectErrorStream(true);

            logger.info("Executing pg_dump to {}", backupFile.getFileName());

            Process process = processBuilder.start();
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                logger.error("pg_dump failed with exit code {}. Output: {}", exitCode, output);
                Files.deleteIfExists(backupFile);
                return false;
            }

            long fileSizeKb = Files.size(backupFile) / 1024;
            logger.info("Database backup completed successfully: {} ({}KB)", filename, fileSizeKb);

            cleanupOldBackups(backupDir);

            return true;
        } catch (IOException | InterruptedException e) {
            logger.error("Database backup failed with exception: {}", e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }

    private void cleanupOldBackups(Path backupDir) {
        File[] backupFiles = backupDir.toFile().listFiles(
                (dir, name) -> name.startsWith("backup_") && name.endsWith(".sql")
        );

        if (backupFiles == null || backupFiles.length <= retentionCount) {
            return;
        }

        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified).reversed());

        for (int i = retentionCount; i < backupFiles.length; i++) {
            if (backupFiles[i].delete()) {
                logger.info("Deleted old backup: {}", backupFiles[i].getName());
            } else {
                logger.warn("Failed to delete old backup: {}", backupFiles[i].getName());
            }
        }
    }

    /**
     * Checks whether any backup DB file exists in the backup directory.
     * A valid backup file matches the pattern backup_*.sql.
     *
     * @return true if at least one backup file exists, false otherwise
     */
    public boolean isBackupFileCreated() {
        Path backupDir = Paths.get(backupDirectory);
        if (!Files.exists(backupDir)) {
            return false;
        }
        File[] backupFiles = backupDir.toFile().listFiles(
                (dir, name) -> name.startsWith("backup_") && name.endsWith(".sql")
        );
        return backupFiles != null && backupFiles.length > 0;
    }

    /**
     * Checks whether a specific backup file exists by filename.
     *
     * @param filename the backup filename to check (e.g. backup_20260321_173500.sql)
     * @return true if the file exists in the backup directory
     */
    public boolean isBackupFileCreated(String filename) {
        Path backupFile = Paths.get(backupDirectory).resolve(filename);
        return Files.exists(backupFile) && Files.isRegularFile(backupFile);
    }

    /**
     * Returns the path to the backup directory.
     */
    public String getBackupDirectory() {
        return backupDirectory;
    }

    private String getRequiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable not set: " + name);
        }
        return value;
    }

    private String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
