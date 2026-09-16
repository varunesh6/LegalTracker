package com.legaltrack.repository;

import com.legaltrack.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByActorUserId(Long actorUserId, Pageable pageable);
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
}
