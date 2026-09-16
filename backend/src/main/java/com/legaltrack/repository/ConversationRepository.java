package com.legaltrack.repository;

import com.legaltrack.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByClientId(Long clientId);
    List<Conversation> findByLawyerId(Long lawyerId);
    Optional<Conversation> findByClientIdAndLawyerIdAndCaseFileId(Long clientId, Long lawyerId, Long caseId);
    Optional<Conversation> findByClientIdAndLawyerId(Long clientId, Long lawyerId);
}
