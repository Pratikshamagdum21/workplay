package com.example.workPay.controller;


import com.example.workPay.entities.Employee;
import com.example.workPay.entities.SalaryType;
import com.example.workPay.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @PutMapping("emp/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id){
        Employee employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("emp/getAllEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(required = false) Integer branchId) {
        if (branchId != null) {
            return ResponseEntity.ok(employeeService.getEmployeesByBranch(branchId));
        }
        return ResponseEntity.ok(employeeService.findAll());
    }

    @PostMapping("emp/saveEmp")
    public ResponseEntity<?> save(
            @RequestParam Integer id,
            @RequestParam String name,
            @RequestParam boolean isBonused,
            @RequestParam String fabricType,
            @RequestParam Integer salary,
            @RequestParam Integer bonusAmount,
            @RequestParam Integer advanceAmount,
            @RequestParam Integer advanceRemaining,
            @RequestParam String salaryType,
            @RequestParam Integer rate,
            @RequestParam Integer clothDoneInMeter,
            @RequestParam(required = false) Integer branchId) {
        Employee employee = Employee.builder()
                .id(id).name(name).isBonused(isBonused).fabricType(fabricType)
                .salary(salary).bonusAmount(bonusAmount).advanceAmount(advanceAmount)
                .advanceRemaining(advanceRemaining)
                .salaryType(SalaryType.valueOf(salaryType.toUpperCase()))
                .rate(rate).clothDoneInMeter(clothDoneInMeter).branchId(branchId)
                .build();
        return employeeService.save(employee)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("emp/deleteEmp")
    void delete(@RequestParam Integer id){
        employeeService.saveHistory(id);
        employeeService.deleteById(id);
    }

    @PatchMapping("/emp/updateEmp/{id}")
    ResponseEntity<Employee> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates){
        Employee updated = employeeService.updateFields(id, updates);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/emp/advancePaid")
    ResponseEntity<Employee> advancePaid(@RequestParam Integer id, @RequestParam Integer advancePaid){
        return ResponseEntity.ok(employeeService.calculateAdvanceRemaining(id, advancePaid));
    }

    @PutMapping("emp/findHistById/{id}")
    ResponseEntity<?> findHistById(@PathVariable Integer id){
        return ResponseEntity.ok(employeeService.findHistById(id));
    }

    @PostMapping("emp/findHistByIdAndDate")
    ResponseEntity<?> findHistByIdAndDate(Integer id, LocalDate targetDate){
        return ResponseEntity.ok(employeeService.findHistByIdAndDate(id, targetDate));
    }
}
