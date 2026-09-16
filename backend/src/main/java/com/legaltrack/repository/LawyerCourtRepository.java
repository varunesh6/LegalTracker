package com.legaltrack.repository;

import com.legaltrack.entity.LawyerCourt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LawyerCourtRepository extends JpaRepository<LawyerCourt, Long> {
    List<LawyerCourt> findByLawyerId(Long lawyerId);
    void deleteByLawyerId(Long lawyerId);
}
