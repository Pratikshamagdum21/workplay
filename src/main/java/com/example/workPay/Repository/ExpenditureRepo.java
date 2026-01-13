package com.example.workPay.Repository;

import com.example.workPay.entities.Expenditure;
import com.example.workPay.entities.ExpenditureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenditureRepo extends JpaRepository<Expenditure, ExpenditureId> {
}
