package com.example.workPay.service;

import com.example.workPay.Repository.EmployeeHistoryRepo;
import com.example.workPay.Repository.EmployeeRepo;
import com.example.workPay.entities.Employee;
import com.example.workPay.entities.EmployeeHistory;
import com.example.workPay.entities.ErrorResponse;
import com.example.workPay.entities.SalaryType;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepo empRepo;

    @Autowired
    private EmployeeHistoryRepo empHistory;

    @Autowired
    EntityManager entityManager;

    public Optional<Employee> save(Employee employee) {

        return Optional.ofNullable(employee)
                .map(empSave -> {
                    Employee employeeSaved = empRepo.save(empSave);
                    EmployeeHistory employeeHistory = new EmployeeHistory();
                    employeeHistory.setDate(LocalDate.now());
                    employeeHistory.setNote("Active Employee");
                    employeeHistory.copyDataFromEmployee(employeeSaved);
                    empHistory.save(employeeHistory);
                    return employeeSaved;
                });

    }

    public List<Employee> findAll() {
        List<Employee> employees = empRepo.findAllByOrderByIdDesc();
        return CollectionUtils.isEmpty(employees) ? Collections.emptyList() : employees;
    }

    public List<Employee> getEmployeesByBranch(Integer branchId) {
        List<Employee> employees = empRepo.findByBranchIdOrderByIdDesc(branchId);
        return CollectionUtils.isEmpty(employees) ? Collections.emptyList() : employees;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteById(Integer id) {

        if (id == null) {
            throw new ErrorResponse("402","Employee id cannot be null");
        }

        empRepo.deleteById(id);
    }

    public Employee findById(Integer id) {
        return empRepo.findById(id)
                .orElseThrow(() -> new ErrorResponse("402", "Employee id not found"));
    }

    public Employee updateFields(Integer id, Map<String, Object> updates) {
        Employee employee = empRepo.findById(id)
                .orElseThrow(() -> new ErrorResponse("402", "Employee not found"));

        updates.forEach((key, value) -> {
            if (value == null) return;
            switch (key) {
                case "name":
                    employee.setName((String) value);
                    break;
                case "salary":
                    employee.setSalary(((Number) value).intValue());
                    break;
                case "isBonused":
                    employee.setIsBonused((Boolean) value);
                    break;
                case "fabricType":
                    employee.setFabricType((String) value);
                    break;
                case "rate":
                    employee.setRate(((Number) value).doubleValue());
                    break;
                case "advanceRemaining":
                    employee.setAdvanceRemaining(((Number) value).intValue());
                    break;
                case "advanceAmount":
                    employee.setAdvanceAmount(((Number) value).intValue());
                    break;
                case "bonusAmount":
                    employee.setBonusAmount(((Number) value).intValue());
                    break;
                case "clothdoneinmeters":
                case "clothDoneInMeter":
                    employee.setClothDoneInMeter(((Number) value).intValue());
                    break;
                case "salaryType":
                    employee.setSalaryType(SalaryType.valueOf(((String) value).toUpperCase()));
                    break;
                default:
                    break;
            }
        });

        if (empHistory.findByEmployeeId(id).isPresent()) {
            EmployeeHistory employeeHistory = new EmployeeHistory();
            employeeHistory.setDate(LocalDate.now());
            employeeHistory.setNote("Active Employee");
            employeeHistory.copyDataFromEmployee(employee);
            empHistory.save(employeeHistory);
        }
        return empRepo.save(employee);
    }

    public Employee calculateAdvanceRemaining(Integer id, Integer advancePaid) {
        Employee employeeAdvancePayment = empRepo.findById(id)
                .filter(employee -> employee.getAdvanceRemaining() > 0 && employee.getAdvanceRemaining() >= advancePaid)
                .map(employee -> {
                    Integer advance = employee.getAdvanceRemaining() - advancePaid;
                    employee.setAdvanceRemaining(advance);
                    return employee;
                })
                .orElseThrow(() -> new ErrorResponse("402", "Advance Remaining is less than expected"));

        empHistory.findById(id).ifPresent(empHist -> {
            EmployeeHistory employeeHistory = new EmployeeHistory();
            employeeHistory.setDate(LocalDate.now());
            employeeHistory.setNote("Active Employee");
            employeeHistory.copyDataFromEmployee(employeeAdvancePayment);
            empHistory.save(employeeHistory);
        });

        return empRepo.save(employeeAdvancePayment);
    }

    @Transactional
    public void saveHistory(Integer id) {
        Employee employee = Optional.of(empRepo.getReferenceById(id))
                .orElseThrow(() ->
                        new ErrorResponse("402", "Employee not found"));
        EmployeeHistory history = new EmployeeHistory();
        history.setNote("Deleted Employee");
        history.setDate(LocalDate.now());
        history.copyDataFromEmployee(employee);
        empHistory.save(history);
    }

    public List<EmployeeHistory> findHistById(Integer id) {
        return empHistory.findByEmployeeId(id)
                .orElseThrow(() -> new ErrorResponse("402", "Employee not found in History"));
    }

    public List<EmployeeHistory> findHistByIdAndDate(Integer id, LocalDate targetDate) {
        return empHistory.findByEmployeeIdAndDate(id, targetDate)
                .orElseThrow(() -> new ErrorResponse("402", "Employee not found in History"));
    }
}
