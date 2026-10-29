package com.uxinterfacely.appraisel.repository;

import com.uxinterfacely.appraisel.entity.Appraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppraisalRepository
        extends JpaRepository<Appraisal, Long> {
}