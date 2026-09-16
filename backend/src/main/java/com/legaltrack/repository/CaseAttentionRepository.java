package com.legaltrack.repository;

import com.legaltrack.entity.CaseAttention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseAttentionRepository extends JpaRepository<CaseAttention, Long> {
    List<CaseAttention> findByCaseFileIdOrderByCreatedAtDesc(Long caseId);
    List<CaseAttention> findByCaseFileIdAndResolvedFalse(Long caseId);
    Optional<CaseAttention> findByCaseFileIdAndTypeAndResolvedFalse(Long caseId, String type);

    @Query("SELECT ca FROM CaseAttention ca WHERE ca.resolved = false AND " +
           "(ca.caseFile.client.id = :userId OR ca.caseFile.lawyer.id = :userId OR " +
           "EXISTS (SELECT tc FROM TrackedCase tc WHERE tc.caseFile = ca.caseFile AND tc.user.id = :userId)) " +
           "ORDER BY ca.createdAt DESC")
    List<CaseAttention> findUnresolvedAttentionForUser(@Param("userId") Long userId);
}
