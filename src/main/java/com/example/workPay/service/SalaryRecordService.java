package com.example.workPay.service;

import com.example.workPay.Repository.EmployeeHistoryRepo;
import com.example.workPay.Repository.EmployeeRepo;
import com.example.workPay.Repository.SalaryRecordRepository;
import com.example.workPay.entities.Employee;
import com.example.workPay.entities.EmployeeHistory;
import com.example.workPay.entities.SalaryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Autowired
    private EmployeeHistoryRepo employeeHistoryRepo;

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

    @Transactional
    public SalaryRecord saveSalary(SalaryRecord record) {
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        SalaryRecord savedRecord = salaryRecordRepository.save(record);

        Employee employee = employeeRepo.findById(record.getEmployeeId().intValue())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + record.getEmployeeId()));

        if (record.getBaseSalary() != null) {
            employee.setSalary(record.getBaseSalary().intValue());
        }
        if (record.getBonus() != null) {
            employee.setBonusAmount(record.getBonus().intValue());
        }
        if (record.getAdvanceTakenTotal() != null) {
            employee.setAdvanceAmount(record.getAdvanceTakenTotal().intValue());
        }
        if (record.getAdvanceRemaining() != null) {
            employee.setAdvanceRemaining(record.getAdvanceRemaining().intValue());
        }
        employee.setClothDoneInMeter(0);

        employeeRepo.save(employee);

        EmployeeHistory history = new EmployeeHistory();
        history.setDate(LocalDate.now());
        history.setNote("Salary Generated");
        history.copyDataFromEmployee(employee);
        employeeHistoryRepo.save(history);

        return savedRecord;
    }
}
