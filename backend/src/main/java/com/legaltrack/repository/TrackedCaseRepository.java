package com.legaltrack.repository;

import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.TrackedCase;
import com.legaltrack.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackedCaseRepository extends JpaRepository<TrackedCase, Long> {
    Optional<TrackedCase> findByUserAndCaseFile(User user, CaseFile caseFile);
    Optional<TrackedCase> findByUserIdAndCaseFileId(Long userId, Long caseId);
    Boolean existsByUserIdAndCaseFileId(Long userId, Long caseId);

    Page<TrackedCase> findByUserId(Long userId, Pageable pageable);
    List<TrackedCase> findByUserId(Long userId);
    List<TrackedCase> findByCaseFileId(Long caseId);

    @Query("SELECT tc FROM TrackedCase tc WHERE tc.user.id = :userId AND " +
           "(:query IS NULL OR LOWER(tc.caseFile.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(tc.caseFile.cnrNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(tc.nickname) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<TrackedCase> searchUserTrackedCases(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    Long countByUserId(Long userId);
}
