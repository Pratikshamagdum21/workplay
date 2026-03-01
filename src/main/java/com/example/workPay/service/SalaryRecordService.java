package com.example.workPay.service;

import com.example.workPay.Repository.SalaryRecordRepository;
import com.example.workPay.entities.SalaryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalaryRecordService {

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    public List<SalaryRecord> getAllSalary(Integer branchId) {
        if (branchId != null) {
            return salaryRecordRepository.findByBranchId(branchId);
        }
        return salaryRecordRepository.findAll();
    }

    public SalaryRecord saveSalary(SalaryRecord record) {
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        return salaryRecordRepository.save(record);
    }
}
