package com.example.workPay.Repository;

import com.example.workPay.entities.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenditureRepo extends JpaRepository<Expenditure, String> {
    Optional<Expenditure> findByIdAndExpenseType(String id, String expenseType);
}
