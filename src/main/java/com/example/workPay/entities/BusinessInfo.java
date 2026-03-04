package com.example.workPay.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String gstin;

    @Column(nullable = false)
    private String state;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "logo_url")
    private String logoUrl;
}
