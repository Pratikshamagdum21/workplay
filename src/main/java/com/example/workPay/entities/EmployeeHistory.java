package com.example.workPay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"EmployeeHistory\"", schema = "public")
public class EmployeeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_id")
    private Integer historyId;
    @Column(name= "emp_id")
    private Integer employeeId;
    @Column(name= "name")
    private String name;
    @Column(name= "\"isBonused\"")
    private Boolean isBonused;
    @Column(name = "\"fabricType\"")
    private String fabricType;
    @Column(name = "\"salary\"")
    private Integer salary;
    @Column(name = "\"bonusAmount\"")
    private Integer bonusAmount;
    @Column(name = "\"advanceAmount\"")
    private Integer advanceAmount;
    @Column(name = "\"advanceRemaining\"")
    private Integer advanceRemaining;
    @Enumerated(EnumType.STRING)
    @Column(name = "\"salaryType\"")
    private SalaryType salaryType;
    @Column(name = "\"rate\"")
    private Integer rate;
    @Column(name = "\"clothDoneInMeter\"")
    private Integer clothDoneInMeter;

    @Column(name = "date")
    private LocalDate date;
    @Column(name = "note")
    private String note;


    public void copyDataFromEmployee(Employee employee){
        this.setName(employee.getName());
        this.setIsBonused(employee.getIsBonused());
        this.setFabricType(employee.getFabricType());
        this.setSalary(employee.getSalary());
        this.setBonusAmount(employee.getBonusAmount());
        this.setAdvanceAmount(employee.getAdvanceAmount());
        this.setAdvanceRemaining(employee.getAdvanceRemaining());
        this.setSalaryType(employee.getSalaryType());
        this.setRate(employee.getRate());
        this.setClothDoneInMeter(employee.getClothDoneInMeter());
        this.setEmployeeId(employee.getId());
    }
}
