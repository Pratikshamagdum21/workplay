package com.example.workPay.service;

import com.example.workPay.Repository.FabricQualityRepository;
import com.example.workPay.entities.FabricQuality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FabricQualityService {

    @Autowired
    private FabricQualityRepository fabricQualityRepository;

    public List<FabricQuality> getAllFabricQualities() {
        return fabricQualityRepository.findAll();
    }

    public FabricQuality createFabricQuality(FabricQuality fabricQuality) {
        return fabricQualityRepository.save(fabricQuality);
    }

    public FabricQuality updateFabricQuality(Long id, FabricQuality fabricQuality) {
        FabricQuality existing = fabricQualityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fabric Quality not found with id: " + id));
        existing.setName(fabricQuality.getName());
        existing.setWidth(fabricQuality.getWidth());
        existing.setFani(fabricQuality.getFani());
        existing.setPeak(fabricQuality.getPeak());
        existing.setWarp(fabricQuality.getWarp());
        existing.setWeft(fabricQuality.getWeft());
        return fabricQualityRepository.save(existing);
    }

    public void deleteFabricQuality(Long id) {
        fabricQualityRepository.deleteById(id);
    }
}
