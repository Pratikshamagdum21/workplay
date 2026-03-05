package com.example.workPay.service;

import com.example.workPay.Repository.BranchRepository;
import com.example.workPay.entities.Branch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @Transactional
    public void clearAllData() {
        entityManager.createNativeQuery("TRUNCATE TABLE salary_record, work_entry, \"Expenditure\", \"EmployeeHistory\", \"Employee\", branch CASCADE").executeUpdate();

        branchRepository.saveAll(List.of(
                Branch.builder().name("Unit 1").code("MB-001").location("Mumbai").build(),
                Branch.builder().name("Unit 2").code("NB-002").location("Delhi").build(),
                Branch.builder().name("Unit 3").code("SB-003").location("Chennai").build()
        ));
    }
}
