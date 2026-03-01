package com.example.workPay.service;

import com.example.workPay.Repository.EmployeeRepo;
import com.example.workPay.Repository.SalaryRecordRepository;
import com.example.workPay.entities.Employee;
import com.example.workPay.entities.SalaryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalaryRecordService {

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private EmployeeRepo employeeRepo;

    public List<SalaryRecord> getAllSalary(Integer branchId) {
        List<SalaryRecord> records;
        if (branchId != null) {
            records = salaryRecordRepository.findByBranchId(branchId);
        } else {
            records = salaryRecordRepository.findAll();
        }

        Map<Integer, String> employeeNameMap = employeeRepo.findAll().stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getName));

        for (SalaryRecord record : records) {
            record.setEmployeeName(employeeNameMap.get(record.getEmployeeId().intValue()));
        }

        return records;
    }

    public SalaryRecord saveSalary(SalaryRecord record) {
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        return salaryRecordRepository.save(record);
    }
}
