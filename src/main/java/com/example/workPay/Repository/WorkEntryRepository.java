package com.example.workPay.Repository;

import com.example.workPay.entities.WorkEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkEntryRepository extends JpaRepository<WorkEntry, String> {
    List<WorkEntry> findByBranchId(Integer branchId);
    List<WorkEntry> findAllByOrderByCreatedAtDesc();
    List<WorkEntry> findByBranchIdOrderByCreatedAtDesc(Integer branchId);
    List<WorkEntry> findByDateBetweenOrderByCreatedAtDesc(LocalDate startDate, LocalDate endDate);
    List<WorkEntry> findByBranchIdAndDateBetweenOrderByCreatedAtDesc(Integer branchId, LocalDate startDate, LocalDate endDate);
}
