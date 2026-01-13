package com.example.workPlay.service;

import com.example.workPlay.Repository.ExpenditureRepo;
import com.example.workPlay.entities.Expenditure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenditureService {
    @Autowired
    private ExpenditureRepo expenditureRepo;


    public Optional<Expenditure> save(Expenditure expenditure) {
        return Optional.ofNullable(expenditure)
                .map(expend -> expenditureRepo.save(expend));

    }

    public List<Expenditure> findAll(){
        return expenditureRepo.findAll();
    }
}
