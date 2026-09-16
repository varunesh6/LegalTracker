package com.legaltrack.repository;

import com.legaltrack.entity.LawyerVerification;
import com.legaltrack.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LawyerVerificationRepository extends JpaRepository<LawyerVerification, Long> {
    Optional<LawyerVerification> findByLawyerId(Long lawyerId);
    Page<LawyerVerification> findByVerificationStatus(VerificationStatus status, Pageable pageable);
}
