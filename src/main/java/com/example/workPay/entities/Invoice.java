package com.example.workPay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private String invoiceDate;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_address")
    private String customerAddress;

    @Column(name = "customer_contact")
    private String customerContact;

    @Column(name = "customer_gstin")
    private String customerGstin;

    @Column(name = "customer_state")
    private String customerState;

    @Column(name = "quality_name")
    private String qualityName;

    private String width;
    private String fani;
    private String peak;
    private String warp;
    private String weft;

    private Integer rolls;
    private Double meters;
    private Double rate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "cgst_amount")
    private Double cgstAmount;

    @Column(name = "sgst_amount")
    private Double sgstAmount;

    @Column(name = "net_payable")
    private Double netPayable;

    @Column(name = "amount_in_words")
    private String amountInWords;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
