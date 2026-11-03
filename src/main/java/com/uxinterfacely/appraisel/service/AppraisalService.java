
package com.uxinterfacely.appraisel.service;

import com.uxinterfacely.appraisel.entity.Appraisal;
import com.uxinterfacely.appraisel.repository.AppraisalRepository;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

    public List<Appraisal> search(String query) {

        String normalized = query == null ? "" : query.trim();

        if (normalized.isEmpty()) {
            return List.of();
        }

        Long id = null;

        try {
            id = Long.valueOf(normalized);
        } catch (NumberFormatException ignored) {
            // Not a numeric appraisal ID
        }

        return repository.searchAppraisals(
                normalized,
                id,
                PageRequest.of(
                        0,
                        50,
                        Sort.by(Sort.Direction.DESC, "id")
                )
        ).getContent();
    }

    public Appraisal getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Appraisal not found: " + id
                        ));
    }
}
