package com.example.demo.controller;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Expenditure;
import com.example.demo.service.ExpenditureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<Expenditure>> getAllExpenditure(){
        return ResponseEntity.ok(expenditureService.findAll());
    }
}
