package com.legaltrack.repository;

import com.legaltrack.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByCourtComplexId(Long courtComplexId);
}
