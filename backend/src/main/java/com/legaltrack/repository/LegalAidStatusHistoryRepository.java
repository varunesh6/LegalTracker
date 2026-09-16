package com.legaltrack.repository;

import com.legaltrack.entity.LegalAidStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalAidStatusHistoryRepository extends JpaRepository<LegalAidStatusHistory, Long> {
    List<LegalAidStatusHistory> findByApplicationIdOrderByChangedAtAsc(Long applicationId);
}
