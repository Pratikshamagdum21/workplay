package com.example.demo.Repository;

import com.example.demo.entities.Expenditure;
import com.example.demo.entities.ExpenditureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenditureRepo extends JpaRepository<Expenditure, ExpenditureId> {
}
