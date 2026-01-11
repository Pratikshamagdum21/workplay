package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "salary")
    private Integer salary;
    @Column(name = "bonusAmount")
    private Integer bonusAmount;
    @Column(name = "advanceAmount")
    private Integer advanceAmount;
}
