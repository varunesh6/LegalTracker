package com.legaltrack.repository;

import com.legaltrack.entity.CaseDiaryEntry;
import com.legaltrack.enums.DiaryVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseDiaryEntryRepository extends JpaRepository<CaseDiaryEntry, Long> {
    List<CaseDiaryEntry> findByCaseFileIdOrderByEventDateDesc(Long caseId);

    @Query("SELECT d FROM CaseDiaryEntry d WHERE d.caseFile.id = :caseId AND " +
           "(d.visibility = 'SHARED' OR " +
           "(d.visibility = 'CLIENT_PRIVATE' AND d.createdBy.id = :userId) OR " +
           "(d.visibility = 'LAWYER_PRIVATE' AND d.createdBy.id = :userId)) " +
           "ORDER BY d.eventDate DESC")
    List<CaseDiaryEntry> findAuthorizedDiaryEntries(@Param("caseId") Long caseId, @Param("userId") Long userId);
}
