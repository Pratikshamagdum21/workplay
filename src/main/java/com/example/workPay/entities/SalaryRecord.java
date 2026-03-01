package com.example.workPay.entities;

import com.example.workPay.config.MeterDetailsConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "salary_record", schema = "public")
public class SalaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "type", nullable = false)
    private String type;

    @Convert(converter = MeterDetailsConverter.class)
    @Column(name = "meter_details_json", columnDefinition = "TEXT")
    private List<Map<String, Object>> meterDetails;

    @Column(name = "rate_per_meter")
    private Double ratePerMeter;

    @Column(name = "total_meters")
    private Double totalMeters;

    @Column(name = "base_salary")
    private Double baseSalary;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "leave_days")
    private Integer leaveDays;

    @Column(name = "leave_deduction_per_day")
    private Double leaveDeductionPerDay;

    @Column(name = "leave_deduction_total")
    private Double leaveDeductionTotal;

    @Column(name = "bonus")
    private Double bonus;

    @Column(name = "advance_taken_total")
    private Double advanceTakenTotal;

    @Column(name = "advance_deducted_this_time")
    private Double advanceDeductedThisTime;

    @Column(name = "advance_remaining")
    private Double advanceRemaining;

    @Column(name = "final_pay")
    private Double finalPay;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "branch_id")
    private Integer branchId;

    @Transient
    private String employeeName;
}
