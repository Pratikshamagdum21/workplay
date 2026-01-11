package com.example.demo.controller;


import com.example.demo.entities.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Employee>> getAllEmployees(){
        return ResponseEntity.ok(employeeService.findAll());
    }

    @PostMapping("emp/saveEmp")
    ResponseEntity<?> save(Employee employee){
        return employeeService.save(employee)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    @DeleteMapping("emp/deleteEmp")
    void delete(Integer id){
        employeeService.deleteById(id);
    }

    @PatchMapping("/emp/{id}")
    ResponseEntity<Employee> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates){
        Employee updated = employeeService.updateFields(id, updates);
        return ResponseEntity.ok(updated);
    }

}
