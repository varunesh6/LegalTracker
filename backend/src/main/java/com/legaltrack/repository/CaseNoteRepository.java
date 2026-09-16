package com.legaltrack.repository;

import com.legaltrack.entity.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseNoteRepository extends JpaRepository<CaseNote, Long> {
    List<CaseNote> findByCaseFileIdAndUserId(Long caseId, Long userId);

    @Query("SELECT n FROM CaseNote n WHERE n.caseFile.id = :caseId AND " +
           "(n.visibility = 'SHARED' OR n.user.id = :userId) " +
           "ORDER BY n.createdAt DESC")
    List<CaseNote> findAuthorizedNotes(@Param("caseId") Long caseId, @Param("userId") Long userId);
}
