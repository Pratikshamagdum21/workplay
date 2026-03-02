package com.example.workPay.controller;

import com.example.workPay.entities.Expenditure;
import com.example.workPay.service.ExpenditureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenditureController {
    @Autowired
    private ExpenditureService expenditureService;

    @PostMapping("expenditure/save")
    public ResponseEntity<?> save(@RequestBody Expenditure expenditure) {
        return expenditureService.save(expenditure).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("expenditure/getAllExpenditure")
    public ResponseEntity<List<Expenditure>> getAllExpenditure() {
        return ResponseEntity.ok(expenditureService.findAll());
    }

    @DeleteMapping("expenditure/delete")
    public ResponseEntity<?> delete(@RequestParam String id, @RequestParam String expenseType) {
        boolean deleted = expenditureService.deleteByIdAndExpenseType(id, expenseType);
        if (deleted) {
            return ResponseEntity.ok().body("Expenditure deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
