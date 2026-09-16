package com.legaltrack.repository;

import com.legaltrack.entity.LegalAidDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalAidDocumentRepository extends JpaRepository<LegalAidDocument, Long> {
    List<LegalAidDocument> findByApplicationId(Long applicationId);
}
