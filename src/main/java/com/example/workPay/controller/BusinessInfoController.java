package com.example.workPay.controller;

import com.example.workPay.entities.BusinessInfo;
import com.example.workPay.service.BusinessInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business-info")
@CrossOrigin(origins = "*")
public class BusinessInfoController {

    @Autowired
    private BusinessInfoService businessInfoService;

    @GetMapping
    public ResponseEntity<BusinessInfo> getBusinessInfo() {
        BusinessInfo info = businessInfoService.getBusinessInfo();
        return ResponseEntity.ok(info);
    }

    @PostMapping
    public ResponseEntity<BusinessInfo> createBusinessInfo(@RequestBody BusinessInfo businessInfo) {
        return ResponseEntity.ok(businessInfoService.createBusinessInfo(businessInfo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessInfo> updateBusinessInfo(@PathVariable Long id,
                                                            @RequestBody BusinessInfo businessInfo) {
        return ResponseEntity.ok(businessInfoService.updateBusinessInfo(id, businessInfo));
    }
}
