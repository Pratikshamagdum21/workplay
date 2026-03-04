package com.example.workPay.service;

import com.example.workPay.Repository.BusinessInfoRepository;
import com.example.workPay.entities.BusinessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessInfoService {

    @Autowired
    private BusinessInfoRepository businessInfoRepository;

    public BusinessInfo getBusinessInfo() {
        List<BusinessInfo> list = businessInfoRepository.findAll();
        return list.isEmpty() ? null : list.get(0);
    }

    public BusinessInfo createBusinessInfo(BusinessInfo businessInfo) {
        return businessInfoRepository.save(businessInfo);
    }

    public BusinessInfo updateBusinessInfo(Long id, BusinessInfo businessInfo) {
        BusinessInfo existing = businessInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business Info not found with id: " + id));
        existing.setBusinessName(businessInfo.getBusinessName());
        existing.setOwnerName(businessInfo.getOwnerName());
        existing.setAddress(businessInfo.getAddress());
        existing.setGstin(businessInfo.getGstin());
        existing.setState(businessInfo.getState());
        existing.setPhoneNumber(businessInfo.getPhoneNumber());
        existing.setLogoUrl(businessInfo.getLogoUrl());
        return businessInfoRepository.save(existing);
    }
}
