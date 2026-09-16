package com.legaltrack.repository;

import com.legaltrack.entity.LegalAidAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LegalAidAssignmentRepository extends JpaRepository<LegalAidAssignment, Long> {
    Optional<LegalAidAssignment> findByApplicationId(Long applicationId);
}
