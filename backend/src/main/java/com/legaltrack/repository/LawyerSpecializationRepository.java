package com.legaltrack.repository;

import com.legaltrack.entity.LawyerSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LawyerSpecializationRepository extends JpaRepository<LawyerSpecialization, Long> {
    List<LawyerSpecialization> findByLawyerId(Long lawyerId);
    void deleteByLawyerId(Long lawyerId);
}
