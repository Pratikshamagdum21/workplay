package com.example.workPay.controller;

import com.example.workPay.entities.FabricQuality;
import com.example.workPay.service.FabricQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fabric-qualities")
@CrossOrigin(origins = "*")
public class FabricQualityController {

    @Autowired
    private FabricQualityService fabricQualityService;

    @GetMapping
    public ResponseEntity<List<FabricQuality>> getAllFabricQualities() {
        return ResponseEntity.ok(fabricQualityService.getAllFabricQualities());
    }

    @PostMapping
    public ResponseEntity<FabricQuality> createFabricQuality(@RequestBody FabricQuality fabricQuality) {
        return ResponseEntity.ok(fabricQualityService.createFabricQuality(fabricQuality));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FabricQuality> updateFabricQuality(@PathVariable Long id,
                                                              @RequestBody FabricQuality fabricQuality) {
        return ResponseEntity.ok(fabricQualityService.updateFabricQuality(id, fabricQuality));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFabricQuality(@PathVariable Long id) {
        fabricQualityService.deleteFabricQuality(id);
        return ResponseEntity.ok().build();
    }
}
