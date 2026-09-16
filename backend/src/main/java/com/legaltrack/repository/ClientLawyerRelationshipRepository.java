package com.legaltrack.repository;

import com.legaltrack.entity.ClientLawyerRelationship;
import com.legaltrack.enums.RelationshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientLawyerRelationshipRepository extends JpaRepository<ClientLawyerRelationship, Long> {
    List<ClientLawyerRelationship> findByClientIdAndStatus(Long clientId, RelationshipStatus status);
    List<ClientLawyerRelationship> findByLawyerIdAndStatus(Long lawyerId, RelationshipStatus status);
    Page<ClientLawyerRelationship> findByClientId(Long clientId, Pageable pageable);
    Page<ClientLawyerRelationship> findByLawyerId(Long lawyerId, Pageable pageable);
    Optional<ClientLawyerRelationship> findByClientIdAndLawyerId(Long clientId, Long lawyerId);
    Optional<ClientLawyerRelationship> findByClientIdAndLawyerIdAndCaseFileId(Long clientId, Long lawyerId, Long caseId);
    Boolean existsByClientIdAndLawyerIdAndStatus(Long clientId, Long lawyerId, RelationshipStatus status);
    Long countByLawyerIdAndStatus(Long lawyerId, RelationshipStatus status);
}
