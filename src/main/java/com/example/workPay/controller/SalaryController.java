package com.example.workPay.controller;

import com.example.workPay.entities.SalaryRecord;
import com.example.workPay.service.SalaryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class SalaryController {

    @Autowired
    private SalaryRecordService salaryRecordService;

    @GetMapping("salary/getAllSalary")
    public ResponseEntity<List<SalaryRecord>> getAllSalary(
            @RequestParam(required = false) Integer branchId) {
        return ResponseEntity.ok(salaryRecordService.getAllSalary(branchId));
    }

    @PostMapping("salary/saveSalary")
    public ResponseEntity<SalaryRecord> saveSalary(@RequestBody SalaryRecord record) {
        return ResponseEntity.ok(salaryRecordService.saveSalary(record));
    }
}
