package com.uxinterfacely.appraisel.repository;

import com.uxinterfacely.appraisel.entity.Appraisal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {

    @Query("""
        SELECT a FROM Appraisal a
        WHERE LOWER(a.employeeName) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.employeeId) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.employeeEmail) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.designation) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.department) LIKE LOWER(CONCAT('%', :q, '%'))
           OR (:id IS NOT NULL AND a.id = :id)
        """)
    Page<Appraisal> searchAppraisals(
            @Param("q") String query,
            @Param("id") Long id,
            Pageable pageable
    );
}
