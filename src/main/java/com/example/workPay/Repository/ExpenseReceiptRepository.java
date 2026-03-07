package com.example.workPay.Repository;

import com.example.workPay.entities.ExpenseReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseReceiptRepository extends JpaRepository<ExpenseReceipt, Long> {
    Optional<ExpenseReceipt> findByExpenseId(String expenseId);
    void deleteByExpenseId(String expenseId);
}
