package com.example.workPay.service;

import com.example.workPay.Repository.*;
import com.example.workPay.entities.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private WorkEntryRepository workEntryRepository;

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private ExpenditureRepo expenditureRepo;

    @Autowired
    private EmployeeHistoryRepo employeeHistoryRepo;

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @Transactional
    public void clearAllData() {
        salaryRecordRepository.deleteAll();
        workEntryRepository.deleteAll();
        expenditureRepo.deleteAll();
        employeeHistoryRepo.deleteAll();
        employeeRepo.deleteAll();
        branchRepository.deleteAll();

        branchRepository.saveAll(List.of(
                Branch.builder().name("Unit 1").code("MB-001").location("Mumbai").build(),
                Branch.builder().name("Unit 2").code("NB-002").location("Delhi").build(),
                Branch.builder().name("Unit 3").code("SB-003").location("Chennai").build()
        ));
    }
}
