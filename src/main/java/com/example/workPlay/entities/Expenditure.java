package com.example.workPlay.entities;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name= "\"amount\"")
    private Integer amount;


    @Column(name= "\"note\"")
    private String note;
}
