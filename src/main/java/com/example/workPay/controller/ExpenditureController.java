package com.example.workPay.controller;

import com.example.workPay.entities.ExpenseReceipt;
import com.example.workPay.entities.Expenditure;
import com.example.workPay.service.ExpenditureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
public class ExpenditureController {
    @Autowired
    private ExpenditureService expenditureService;

    @PostMapping(value = "expenditure/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveWithImage(
            @RequestPart("expenditure") String expenditureJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Expenditure expenditure = mapper.readValue(expenditureJson, Expenditure.class);

            return expenditureService.save(expenditure, images)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save expenditure: " + e.getMessage());
        }
    }

    @PostMapping(value = "expenditure/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody Expenditure expenditure) {
        return expenditureService.save(expenditure).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("expenditure/getAllExpenditure")
    public ResponseEntity<?> getAllExpenditure(
            @RequestParam(required = false) Integer branchId) {
        List<Expenditure> expenditures = expenditureService.findAll(branchId);

        List<ExpenditureWithReceipts> result = expenditures.stream()
                .map(exp -> {
                    List<Long> receiptIds = expenditureService.getReceiptsByExpenseId(exp.getId())
                            .stream()
                            .map(ExpenseReceipt::getId)
                            .toList();
                    return new ExpenditureWithReceipts(exp, receiptIds);
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("expenditure/{expenseId}/receipts")
    public ResponseEntity<?> getReceipts(@PathVariable String expenseId) {
        List<ExpenseReceipt> receipts = expenditureService.getReceiptsByExpenseId(expenseId);
        if (receipts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ReceiptInfo> receiptInfos = receipts.stream()
                .map(r -> new ReceiptInfo(r.getId(), r.getFileName(), r.getFileType(), r.getUploadedAt()))
                .toList();

        return ResponseEntity.ok(receiptInfos);
    }

    @GetMapping("expenditure/receipt/{receiptId}")
    public ResponseEntity<byte[]> getReceiptImage(@PathVariable Long receiptId) {
        Optional<ExpenseReceipt> receipt = expenditureService.getReceiptById(receiptId);

        if (receipt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ExpenseReceipt expenseReceipt = receipt.get();
        String contentType = expenseReceipt.getFileType() != null
                ? expenseReceipt.getFileType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + expenseReceipt.getFileName() + "\"")
                .body(expenseReceipt.getImageData());
    }

    @DeleteMapping("expenditure/receipt/{receiptId}")
    public ResponseEntity<?> deleteReceipt(@PathVariable Long receiptId) {
        if (expenditureService.deleteReceipt(receiptId)) {
            return ResponseEntity.ok().body("Receipt deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "expenditure/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateWithImage(
            @PathVariable String id,
            @RequestPart("expenditure") String expenditureJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Expenditure expenditure = mapper.readValue(expenditureJson, Expenditure.class);

            return expenditureService.update(id, expenditure, images)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update expenditure: " + e.getMessage());
        }
    }

    @PutMapping(value = "expenditure/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Expenditure expenditure) {
        return expenditureService.update(id, expenditure)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("expenditure/delete")
    public ResponseEntity<?> delete(@RequestParam String id, @RequestParam String expenseType) {
        boolean deleted = expenditureService.deleteByIdAndExpenseType(id, expenseType);
        if (deleted) {
            return ResponseEntity.ok().body("Expenditure deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    private record ReceiptInfo(Long id, String fileName, String fileType, java.time.LocalDateTime uploadedAt) {}

    private record ExpenditureWithReceipts(Expenditure expenditure, List<Long> receiptIds) {}
}
