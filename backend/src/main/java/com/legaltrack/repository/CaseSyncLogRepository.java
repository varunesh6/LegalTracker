package com.legaltrack.repository;

import com.legaltrack.entity.CaseSyncLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseSyncLogRepository extends JpaRepository<CaseSyncLog, Long> {
    List<CaseSyncLog> findByCaseFileIdOrderByStartedAtDesc(Long caseId);
    Page<CaseSyncLog> findAllByOrderByStartedAtDesc(Pageable pageable);
}
