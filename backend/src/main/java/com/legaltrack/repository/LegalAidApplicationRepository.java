package com.legaltrack.repository;

import com.legaltrack.entity.LegalAidApplication;
import com.legaltrack.enums.LegalAidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LegalAidApplicationRepository extends JpaRepository<LegalAidApplication, Long> {
    Optional<LegalAidApplication> findByApplicationNumber(String applicationNumber);
    Page<LegalAidApplication> findByClientId(Long clientId, Pageable pageable);
    Page<LegalAidApplication> findByStatus(LegalAidStatus status, Pageable pageable);
    Page<LegalAidApplication> findByAssignedLawyerId(Long lawyerId, Pageable pageable);

    @Query("SELECT a FROM LegalAidApplication a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:districtId IS NULL OR a.district.id = :districtId) AND " +
           "(:query IS NULL OR LOWER(a.applicationNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<LegalAidApplication> searchApplications(
            @Param("status") LegalAidStatus status,
            @Param("districtId") Long districtId,
            @Param("query") String query,
            Pageable pageable
    );

    Long countByStatus(LegalAidStatus status);
}
