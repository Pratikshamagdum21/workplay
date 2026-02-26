package com.example.workPay.Repository;

import com.example.workPay.entities.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
    List<SalaryRecord> findByBranchId(Integer branchId);
    List<SalaryRecord> findByEmployeeIdAndCreatedAtBetween(
            Long employeeId, LocalDateTime start, LocalDateTime end);
}
