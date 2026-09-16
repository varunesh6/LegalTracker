package com.legaltrack.repository;

import com.legaltrack.entity.LawyerLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LawyerLanguageRepository extends JpaRepository<LawyerLanguage, Long> {
    List<LawyerLanguage> findByLawyerId(Long lawyerId);
    void deleteByLawyerId(Long lawyerId);
}
