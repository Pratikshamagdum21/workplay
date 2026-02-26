package com.example.workPay.controller;

import com.example.workPay.entities.WorkEntry;
import com.example.workPay.service.WorkEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class WorkController {

    @Autowired
    private WorkEntryService workEntryService;

    @GetMapping("work/getAllWork")
    public ResponseEntity<List<WorkEntry>> getAllWork(
            @RequestParam(required = false) Integer branchId) {
        return ResponseEntity.ok(workEntryService.getAllWork(branchId));
    }

    @PostMapping("work/saveWork")
    public ResponseEntity<WorkEntry> saveWork(@RequestBody WorkEntry entry) {
        return ResponseEntity.ok(workEntryService.saveWork(entry));
    }
}
