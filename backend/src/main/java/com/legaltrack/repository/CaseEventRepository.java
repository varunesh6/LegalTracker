package com.legaltrack.repository;

import com.legaltrack.entity.CaseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseEventRepository extends JpaRepository<CaseEvent, Long> {
    List<CaseEvent> findByCaseFileIdOrderByEventDateDesc(Long caseId);
    List<CaseEvent> findByCaseFileIdOrderByEventDateAsc(Long caseId);
}
