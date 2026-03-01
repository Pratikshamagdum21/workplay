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
                    Branch.builder().name("Main Branch").code("MB-001").location("Mumbai").build(),
                    Branch.builder().name("North Branch").code("NB-002").location("Delhi").build(),
                    Branch.builder().name("South Branch").code("SB-003").location("Chennai").build()
            ));
        }
    }
}
