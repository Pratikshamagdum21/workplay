package com.example.workPay.Repository;

import com.example.workPay.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    List<Employee> findByBranchId(Integer branchId);
    List<Employee> findAllByOrderByIdDesc();
    List<Employee> findByBranchIdOrderByIdDesc(Integer branchId);
}
