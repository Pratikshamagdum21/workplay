package com.example.workPlay.Repository;

import com.example.workPlay.entities.Expenditure;
import com.example.workPlay.entities.ExpenditureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenditureRepo extends JpaRepository<Expenditure, ExpenditureId> {
}
