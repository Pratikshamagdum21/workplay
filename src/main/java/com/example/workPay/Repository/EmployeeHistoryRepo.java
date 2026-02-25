package com.example.workPay.Repository;

import com.example.workPay.entities.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeHistoryRepo extends JpaRepository<EmployeeHistory, Integer> {
    public Optional<List<EmployeeHistory>> findByEmployeeId(Integer id);

    @Query(value = """
    SELECT *
    FROM \"EmployeeHistory\"
    WHERE emp_id = :id
      AND date <= :targetDate
    ORDER BY date DESC
""", nativeQuery = true)
    public Optional<List<EmployeeHistory>> findByEmployeeIdAndDate(Integer id, LocalDate targetDate);
}
