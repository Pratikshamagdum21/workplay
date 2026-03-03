package com.example.workPay.service;

import com.example.workPay.Repository.ExpenditureRepo;
import com.example.workPay.entities.Expenditure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenditureService {
    @Autowired
    private ExpenditureRepo expenditureRepo;

    public Optional<Expenditure> save(Expenditure expenditure) {
        return Optional.ofNullable(expenditure)
                .map(expend -> expenditureRepo.save(expend));
    }

    public List<Expenditure> findAll(Integer branchId) {
        if (branchId != null) {
            return expenditureRepo.findByBranchIdOrderByDateDesc(branchId);
        }
        return expenditureRepo.findAllByOrderByDateDesc();
    }

    public boolean deleteByIdAndExpenseType(String id, String expenseType) {
        Optional<Expenditure> expenditure = expenditureRepo.findByIdAndExpenseType(id, expenseType);
        if (expenditure.isPresent()) {
            expenditureRepo.delete(expenditure.get());
            return true;
        }
        return false;
    }
}
