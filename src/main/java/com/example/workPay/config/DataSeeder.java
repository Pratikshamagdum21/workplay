package com.example.workPay.config;

import com.example.workPay.Repository.BranchRepository;
import com.example.workPay.entities.Branch;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class DataSeeder {

    @Autowired
    private BranchRepository branchRepository;

    /**
     * Seed initial branch data. This is now @Lazy — it will only execute
     * when explicitly triggered or when a bean depends on it,
     * NOT on every app startup (which would wake Neon compute).
     *
     * To run manually: call POST /admin/seed-branches or trigger on first real request.
     */
    public void seedBranches() {
        if (branchRepository.count() == 0) {
            branchRepository.saveAll(List.of(
                    Branch.builder().name("Unit 1").code("MB-001").location("Mumbai").build(),
                    Branch.builder().name("Unit 2").code("NB-002").location("Delhi").build(),
                    Branch.builder().name("Unit 3").code("SB-003").location("Chennai").build()
            ));
        }
    }
}
