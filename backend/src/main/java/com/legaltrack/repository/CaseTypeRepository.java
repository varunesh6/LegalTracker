package com.legaltrack.repository;

import com.legaltrack.entity.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseTypeRepository extends JpaRepository<CaseType, Long> {
    Optional<CaseType> findByCode(String code);
    Optional<CaseType> findByName(String name);
    List<CaseType> findByCategory(String category);
}
