package com.example.workPay.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YearlyDataMigrationRequest {

    private List<EmployeeYearlyData> employees;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeYearlyData {
        private Integer employeeId;
        private Integer salary;
        private Integer advanceAmount;
        private Integer advanceRemaining;
    }
}
