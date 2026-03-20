package com.example.workPay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Lightweight liveness probe — no DB call.
     * Use this for cron/uptime monitoring to avoid waking Neon compute.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    /**
     * Deep health check that verifies DB connectivity.
     * Only call this when you actually need to confirm the database is reachable.
     * WARNING: This WILL wake up a suspended Neon compute instance.
     */
    @GetMapping("/health/db")
    public ResponseEntity<Map<String, String>> healthDb() {
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute("SELECT 1");
            return ResponseEntity.ok(Map.of("status", "UP", "database", "connected"));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "database", "disconnected",
                "error", e.getMessage()
            ));
        }
    }
}
