package com.legaltrack.repository;

import com.legaltrack.entity.CaseAdvocate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseAdvocateRepository extends JpaRepository<CaseAdvocate, Long> {
    List<CaseAdvocate> findByCaseFileId(Long caseId);
}
