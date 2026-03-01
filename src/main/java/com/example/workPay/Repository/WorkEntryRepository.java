package com.example.workPay.Repository;

import com.example.workPay.entities.WorkEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkEntryRepository extends JpaRepository<WorkEntry, String> {
    List<WorkEntry> findByBranchId(Integer branchId);
}
