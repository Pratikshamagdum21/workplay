package com.example.demo.service;

import com.example.demo.Repository.EmployeeRepo;
import com.example.demo.entities.Employee;
import com.example.demo.entities.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepo emp;

    public Optional<Employee> save(Employee employee) {
        return Optional.ofNullable(employee)
                .map(empSave -> emp.save(employee));

    }

    public List<Employee> findAll() {
        return CollectionUtils.isEmpty(emp.findAll()) ? Collections.emptyList() : emp.findAll();
    }

    public void deleteById(Integer id) {
        Optional.ofNullable(id)
                .ifPresent(empId -> {
                    if (!emp.existsById(empId))
                        throw new ErrorResponse("402", "Employee Not found");
                    emp.deleteById(id);
                });
    }

    public Employee findById(Integer id) {
        return emp.findById(id)
                .orElseThrow(() -> new ErrorResponse("402", "397"));
    }

    public Employee updateFields(Integer id, Map<String, Object> updates) {
        Employee employee = emp.findById(id).orElseThrow(() -> new ErrorResponse("402", "397"));
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    employee.setName((String) value);
                    break;
                case "salary":
                    employee.setSalary((Integer) value);
                    break;

                case "isBonused":
                    employee.setIsBonused((Boolean) value);
                    break;

                case "fabricType":
                    employee.setFabricType((String) value);
                    break;

                default:
                    throw new ErrorResponse("402", "Invalid field");
            }
        });
        return emp.save(employee);
    }
}
