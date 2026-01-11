package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Employee\"", schema = "public")
@Embeddable
public class ExpenditureId {

    @Column(name = "expenseDate")
    private LocalDate date;

    @Column(name = "expenseType")
    private String expenseType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenditureId)) return false;
        ExpenditureId that = (ExpenditureId) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(expenseType, that.expenseType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseType, date);
    }
}
