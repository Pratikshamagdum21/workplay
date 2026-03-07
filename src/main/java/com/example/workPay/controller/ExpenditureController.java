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
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Expenditure expenditure = mapper.readValue(expenditureJson, Expenditure.class);

            return expenditureService.save(expenditure, image)
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
    public ResponseEntity<List<Expenditure>> getAllExpenditure(
            @RequestParam(required = false) Integer branchId) {
        return ResponseEntity.ok(expenditureService.findAll(branchId));
    }

    @GetMapping("expenditure/{expenseId}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable String expenseId) {
        Optional<ExpenseReceipt> receipt = expenditureService.getReceiptByExpenseId(expenseId);

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

    @DeleteMapping("expenditure/delete")
    public ResponseEntity<?> delete(@RequestParam String id, @RequestParam String expenseType) {
        boolean deleted = expenditureService.deleteByIdAndExpenseType(id, expenseType);
        if (deleted) {
            return ResponseEntity.ok().body("Expenditure deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
