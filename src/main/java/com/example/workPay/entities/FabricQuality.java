package com.example.workPay.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fabric_qualities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FabricQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String width;
    private String fani;
    private String peak;
    private String warp;
    private String weft;
}
