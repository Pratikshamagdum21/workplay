package com.example.workPay.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Service
public class DatabaseBackupService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Autowired
    private DataSource dataSource;

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
        Path backupDir = Paths.get(backupDirectory);
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("backup_%s.sql", timestamp);
        Path backupFile = backupDir.resolve(filename);

        try {
            Files.createDirectories(backupDir);

            try (Connection conn = dataSource.getConnection();
                 BufferedWriter writer = Files.newBufferedWriter(backupFile)) {

                writer.write("-- Database backup created at " + LocalDateTime.now());
                writer.newLine();
                writer.newLine();

                List<String> tables = getTableNames(conn);
                logger.info("Found {} tables to back up", tables.size());

                for (String table : tables) {
                    exportTable(conn, table, writer);
                }
            }

            long fileSizeKb = Files.size(backupFile) / 1024;
            logger.info("Database backup completed successfully: {} ({}KB)", filename, fileSizeKb);

            uploadToGitHub(backupFile, filename);

            cleanupOldBackups(backupDir);
            return true;

        } catch (SQLException | IOException e) {
            logger.error("Database backup failed: {}", e.getMessage(), e);
            try {
                Files.deleteIfExists(backupFile);
            } catch (IOException deleteEx) {
                logger.warn("Failed to delete incomplete backup file: {}", deleteEx.getMessage());
            }
            return false;
        }
    }

    private List<String> getTableNames(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet rs = metaData.getTables(null, "public", null, new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    private void exportTable(Connection conn, String table, BufferedWriter writer)
            throws SQLException, IOException {
        // Quote the table name to handle case-sensitive and reserved-word names
        String quotedTable = "\"" + table + "\"";
        writer.write("-- Table: " + table);
        writer.newLine();

        // Get column names and types
        List<String> columns = new ArrayList<>();
        List<Integer> columnTypes = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getColumns(null, "public", table, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
                columnTypes.add(rs.getInt("DATA_TYPE"));
            }
        }

        if (columns.isEmpty()) {
            writer.newLine();
            return;
        }

        // Export rows as INSERT statements
        String selectSql = "SELECT * FROM " + quotedTable;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {

            int rowCount = 0;
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO ").append(quotedTable).append(" (");

                for (int i = 0; i < columns.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append("\"").append(columns.get(i)).append("\"");
                }
                sb.append(") VALUES (");

                for (int i = 0; i < columns.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(formatValue(rs, i + 1, columnTypes.get(i)));
                }
                sb.append(");");

                writer.write(sb.toString());
                writer.newLine();
                rowCount++;
            }
            logger.info("Exported {} rows from table {}", rowCount, table);
        }
        writer.newLine();
    }

    private String formatValue(ResultSet rs, int columnIndex, int sqlType) throws SQLException {
        Object value = rs.getObject(columnIndex);
        if (value == null) {
            return "NULL";
        }

        switch (sqlType) {
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.NUMERIC:
                return value.toString();
            case Types.BOOLEAN:
            case Types.BIT:
                return Boolean.TRUE.equals(rs.getBoolean(columnIndex)) ? "TRUE" : "FALSE";
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                byte[] bytes = rs.getBytes(columnIndex);
                return "E'\\\\x" + bytesToHex(bytes) + "'";
            default:
                // Strings, dates, timestamps, etc. — escape single quotes
                String strVal = value.toString();
                strVal = strVal.replace("'", "''");
                return "'" + strVal + "'";
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    private void uploadToGitHub(Path backupFile, String filename) {
        String token = System.getenv("GITHUB_BACKUP_TOKEN");
        String repo = System.getenv("GITHUB_BACKUP_REPO");

        if (token == null || token.isBlank() || repo == null || repo.isBlank()) {
            logger.warn("GitHub backup upload skipped — GITHUB_BACKUP_TOKEN or GITHUB_BACKUP_REPO not set");
            return;
        }

        try {
            byte[] fileBytes = Files.readAllBytes(backupFile);
            String base64Content = Base64.getEncoder().encodeToString(fileBytes);

            String jsonBody = "{\"message\":\"Backup " + filename + "\","
                    + "\"content\":\"" + base64Content + "\"}";

            String apiUrl = "https://api.github.com/repos/" + repo + "/contents/" + filename;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                logger.info("Backup uploaded to GitHub successfully: {}", filename);
            } else {
                logger.error("GitHub upload failed (HTTP {}): {}", response.statusCode(), response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("GitHub upload failed: {}", e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
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
}
