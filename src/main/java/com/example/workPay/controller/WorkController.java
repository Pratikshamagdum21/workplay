package com.example.workPay.controller;

import com.example.workPay.entities.WorkEntry;
import com.example.workPay.service.WorkEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class WorkController {

    @Autowired
    private WorkEntryService workEntryService;

    @GetMapping("work/getAllWork")
    public ResponseEntity<List<WorkEntry>> getAllWork(
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(workEntryService.getAllWork(branchId, startDate, endDate));
    }

    @PostMapping("work/saveWork")
    public ResponseEntity<WorkEntry> saveWork(@RequestBody WorkEntry entry) {
        return ResponseEntity.ok(workEntryService.saveWork(entry));
    }

    @PutMapping("work/updateWork/{id}")
    public ResponseEntity<WorkEntry> updateWork(@PathVariable String id, @RequestBody WorkEntry entry) {
        return workEntryService.updateWork(id, entry)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("work/deleteWork")
    public ResponseEntity<?> deleteWork(@RequestParam String id) {
        boolean deleted = workEntryService.deleteWork(id);
        if (deleted) {
            return ResponseEntity.ok().body("Work entry deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
