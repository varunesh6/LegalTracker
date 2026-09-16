package com.legaltrack.repository;

import com.legaltrack.entity.LawyerAvailability;
import com.legaltrack.entity.LawyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LawyerAvailabilityRepository extends JpaRepository<LawyerAvailability, Long> {
    Optional<LawyerAvailability> findByLawyer(LawyerProfile lawyer);
    Optional<LawyerAvailability> findByLawyerId(Long lawyerId);
}
