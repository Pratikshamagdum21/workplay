package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"Expenditure\"", schema = "public")
public class Expenditure {
    @Id
    @EmbeddedId
    private ExpenditureId id;

    @Column(name= "amount")
    private Integer amount;


    @Column(name= "note")
    private String note;


}
