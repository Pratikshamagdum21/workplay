package com.example.workPay.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"Employee\"", schema = "public")
public class Employee {
    @Id
    @Column(name= "id")
    private Integer id;
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

    @Column(name = "branch_id")
    private Integer branchId;

}
