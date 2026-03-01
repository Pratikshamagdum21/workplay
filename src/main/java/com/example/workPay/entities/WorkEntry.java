package com.example.workPay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "work_entry", schema = "public")
public class WorkEntry {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "employee_type", nullable = false)
    private String employeeType;

    @Column(name = "shift", nullable = false)
    private String shift;

    @Column(name = "fabric_meters", nullable = false)
    private Double fabricMeters;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "branch_id")
    private Integer branchId;
}
