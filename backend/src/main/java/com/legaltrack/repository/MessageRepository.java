package com.legaltrack.repository;

import com.legaltrack.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdAndDeletedAtIsNullOrderBySentAtAsc(Long conversationId);
    Page<Message> findByConversationIdAndDeletedAtIsNullOrderBySentAtDesc(Long conversationId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.sender.id != :userId AND m.readAt IS NULL")
    Long countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}
