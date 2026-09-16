package com.legaltrack.repository;

import com.legaltrack.entity.CaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseOrderRepository extends JpaRepository<CaseOrder, Long> {
    List<CaseOrder> findByCaseFileIdOrderByOrderDateDesc(Long caseId);
}
