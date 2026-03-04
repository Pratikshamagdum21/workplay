package com.example.workPay.Repository;

import com.example.workPay.entities.FabricQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FabricQualityRepository extends JpaRepository<FabricQuality, Long> {
}
