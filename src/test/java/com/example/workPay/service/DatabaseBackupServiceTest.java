package com.example.workPay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DatabaseBackupServiceTest {

    private DatabaseBackupService backupService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        backupService = new DatabaseBackupService();
        // Inject the temp directory as the backup directory via reflection
        Field backupDirField = DatabaseBackupService.class.getDeclaredField("backupDirectory");
        backupDirField.setAccessible(true);
        backupDirField.set(backupService, tempDir.toString());

        // Inject a mock DataSource (not used by isBackupFileCreated methods)
        Field dataSourceField = DatabaseBackupService.class.getDeclaredField("dataSource");
        dataSourceField.setAccessible(true);
        dataSourceField.set(backupService, mock(DataSource.class));
    }

    @Test
    void isBackupFileCreated_returnsFalseWhenNoBackupsExist() {
        assertFalse(backupService.isBackupFileCreated(),
                "Should return false when backup directory is empty");
    }

    @Test
    void isBackupFileCreated_returnsTrueWhenBackupFileExists() throws IOException {
        // Create a backup file matching the expected pattern
        Files.createFile(tempDir.resolve("backup_20260321_173500.sql"));

        assertTrue(backupService.isBackupFileCreated(),
                "Should return true when a backup file exists");
    }

    @Test
    void isBackupFileCreated_ignoresNonBackupFiles() throws IOException {
        // Create files that don't match the backup pattern
        Files.createFile(tempDir.resolve("random_file.txt"));
        Files.createFile(tempDir.resolve("backup_.txt"));
        Files.createFile(tempDir.resolve("data.sql"));

        assertFalse(backupService.isBackupFileCreated(),
                "Should return false when only non-matching files exist");
    }

    @Test
    void isBackupFileCreated_withFilename_returnsTrueForExistingFile() throws IOException {
        String filename = "backup_20260321_173500.sql";
        Files.createFile(tempDir.resolve(filename));

        assertTrue(backupService.isBackupFileCreated(filename),
                "Should return true for an existing backup file by name");
    }

    @Test
    void isBackupFileCreated_withFilename_returnsFalseForMissingFile() {
        assertFalse(backupService.isBackupFileCreated("backup_20260321_173500.sql"),
                "Should return false when the specified file does not exist");
    }

    @Test
    void isBackupFileCreated_returnsFalseWhenDirectoryDoesNotExist() throws Exception {
        // Point to a non-existent directory
        Field backupDirField = DatabaseBackupService.class.getDeclaredField("backupDirectory");
        backupDirField.setAccessible(true);
        backupDirField.set(backupService, tempDir.resolve("nonexistent").toString());

        assertFalse(backupService.isBackupFileCreated(),
                "Should return false when the backup directory does not exist");
    }

    @Test
    void isBackupFileCreated_detectsMultipleBackups() throws IOException {
        Files.createFile(tempDir.resolve("backup_20260320_020000.sql"));
        Files.createFile(tempDir.resolve("backup_20260321_020000.sql"));
        Files.createFile(tempDir.resolve("backup_20260322_020000.sql"));

        assertTrue(backupService.isBackupFileCreated(),
                "Should return true when multiple backup files exist");
    }
}
