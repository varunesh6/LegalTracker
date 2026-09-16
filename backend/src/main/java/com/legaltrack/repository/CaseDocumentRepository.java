package com.legaltrack.repository;

import com.legaltrack.entity.CaseDocument;
import com.legaltrack.enums.DocumentVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseDocumentRepository extends JpaRepository<CaseDocument, Long> {
    List<CaseDocument> findByCaseFileIdOrderByUploadedAtDesc(Long caseId);
    List<CaseDocument> findByParentDocumentIdOrderByVersionAsc(Long parentId);

    @Query("SELECT d FROM CaseDocument d WHERE d.caseFile.id = :caseId AND " +
           "(d.visibility = 'CLIENT_AND_LAWYER' OR " +
           "d.visibility = 'PUBLIC_COURT_RECORD' OR " +
           "(d.visibility = 'CLIENT_ONLY' AND d.uploadedBy.id = :userId) OR " +
           "(d.visibility = 'LAWYER_ONLY' AND d.uploadedBy.id = :userId) OR " +
           ":isAdmin = TRUE) ORDER BY d.uploadedAt DESC")
    List<CaseDocument> findAuthorizedDocuments(
            @Param("caseId") Long caseId,
            @Param("userId") Long userId,
            @Param("isAdmin") boolean isAdmin
    );
}
