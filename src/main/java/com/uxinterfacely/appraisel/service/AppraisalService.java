package com.uxinterfacely.appraisel.service;

import com.uxinterfacely.appraisel.entity.Appraisal;
import com.uxinterfacely.appraisel.repository.AppraisalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppraisalService {

    private final AppraisalRepository repository;

    public AppraisalService(AppraisalRepository repository) {
        this.repository = repository;
    }

    public Appraisal save(Appraisal appraisal) {
        return repository.save(appraisal);
    }

    public List<Appraisal> getAll() {
        return repository.findAll();
    }

    public Appraisal getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Appraisal not found: " + id));
    }
}