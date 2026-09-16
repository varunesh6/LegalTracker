package com.legaltrack.repository;

import com.legaltrack.entity.SupportTicket;
import com.legaltrack.enums.SupportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    Page<SupportTicket> findByUserId(Long userId, Pageable pageable);
    Page<SupportTicket> findByStatus(SupportStatus status, Pageable pageable);
    Long countByStatus(SupportStatus status);
}
