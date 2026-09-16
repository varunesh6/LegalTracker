package com.legaltrack.repository;

import com.legaltrack.entity.CaseParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasePartyRepository extends JpaRepository<CaseParty, Long> {
    List<CaseParty> findByCaseFileId(Long caseId);
}
