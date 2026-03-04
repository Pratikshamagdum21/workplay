package com.example.workPay.controller;

import com.example.workPay.dto.BulkDownloadRequest;
import com.example.workPay.entities.BusinessInfo;
import com.example.workPay.entities.Invoice;
import com.example.workPay.service.BusinessInfoService;
import com.example.workPay.service.InvoicePdfService;
import com.example.workPay.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private BusinessInfoService businessInfoService;

    @Autowired
    private InvoicePdfService invoicePdfService;

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        BusinessInfo business = businessInfoService.getBusinessInfo();

        byte[] pdfBytes = invoicePdfService.generateSingleInvoicePdf(invoice, business);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "Invoice_" + invoice.getInvoiceNumber() + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @PostMapping("/download-bulk")
    public ResponseEntity<byte[]> downloadBulkInvoices(@RequestBody BulkDownloadRequest request) {
        List<Invoice> invoices = invoiceService.getInvoicesByIds(request.getInvoiceIds());
        BusinessInfo business = businessInfoService.getBusinessInfo();

        byte[] pdfBytes = invoicePdfService.generateBulkInvoicePdf(invoices, business);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Invoices_Bulk.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
