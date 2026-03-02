package com.example.workPay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"Expenditure\"", schema = "public")
public class Expenditure {
    @Id
    @Column(name = "\"id\"")
    private String id;

    @Column(name = "\"date\"")
    private LocalDate date;

    @Column(name = "\"expenseType\"")
    private String expenseType;

    @Column(name= "\"amount\"")
    private Integer amount;

    @Column(name= "\"note\"")
    private String note;

    @Column(name = "\"branchId\"")
    private Integer branchId;

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }
    }
}
