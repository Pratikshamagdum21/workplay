package com.example.workPay.service;

import com.example.workPay.Repository.WorkEntryRepository;
import com.example.workPay.entities.WorkEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WorkEntryService {

    @Autowired
    private WorkEntryRepository workEntryRepository;

    public List<WorkEntry> getAllWork(Integer branchId) {
        if (branchId != null) {
            return workEntryRepository.findByBranchId(branchId);
        }
        return workEntryRepository.findAll();
    }

    public WorkEntry saveWork(WorkEntry entry) {
        if (entry.getId() == null || entry.getId().isBlank()) {
            entry.setId(UUID.randomUUID().toString());
        }
        if (entry.getCreatedAt() == null) {
            entry.setCreatedAt(LocalDateTime.now());
        }
        return workEntryRepository.save(entry);
    }
}
