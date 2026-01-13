package com.example.workPlay.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Expenditure\"", schema = "public")
@Embeddable
public class ExpenditureId {

    @Column(name = "\"date\"")
    private LocalDate date;

    @Column(name = "\"expenseType\"")
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
