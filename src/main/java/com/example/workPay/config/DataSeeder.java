package com.example.workPay.config;

import com.example.workPay.Repository.BranchRepository;
import com.example.workPay.entities.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public void run(String... args) throws Exception {
        if (branchRepository.count() == 0) {
            branchRepository.saveAll(List.of(
                    Branch.builder().name("Unit 1").code("MB-001").location("Mumbai").build(),
                    Branch.builder().name("Unit 2").code("NB-002").location("Delhi").build(),
                    Branch.builder().name("Unit 3").code("SB-003").location("Chennai").build(),
                    Branch.builder().name("Unit 4").code("KB-004").location("Kolkata").build()
            ));
        }
    }
}
