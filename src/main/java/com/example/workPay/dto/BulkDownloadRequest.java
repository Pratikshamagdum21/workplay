package com.example.workPay.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkDownloadRequest {
    private List<Long> invoiceIds;
}
