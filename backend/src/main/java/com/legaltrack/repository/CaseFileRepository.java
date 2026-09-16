package com.legaltrack.repository;

import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.User;
import com.legaltrack.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseFileRepository extends JpaRepository<CaseFile, Long> {
    Optional<CaseFile> findByInternalReferenceId(String internalReferenceId);
    Optional<CaseFile> findByCnrNumber(String cnrNumber);
    Optional<CaseFile> findByCaseNumber(String caseNumber);
    Optional<CaseFile> findByFilingNumber(String filingNumber);
    Optional<CaseFile> findByFirNumber(String firNumber);

    Page<CaseFile> findByClientId(Long clientId, Pageable pageable);
    Page<CaseFile> findByLawyerId(Long lawyerId, Pageable pageable);
    Page<CaseFile> findByStatus(CaseStatus status, Pageable pageable);

    @Query("SELECT c FROM CaseFile c WHERE " +
           "(:clientId IS NULL OR c.client.id = :clientId) AND " +
           "(:lawyerId IS NULL OR c.lawyer.id = :lawyerId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:query IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.cnrNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.caseNumber) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<CaseFile> searchCases(
            @Param("clientId") Long clientId,
            @Param("lawyerId") Long lawyerId,
            @Param("status") CaseStatus status,
            @Param("query") String query,
            Pageable pageable
    );

    List<CaseFile> findByNextHearingDateBetween(LocalDate start, LocalDate end);
    Long countByStatus(CaseStatus status);
}
