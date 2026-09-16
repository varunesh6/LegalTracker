package com.legaltrack.entity;

import com.legaltrack.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_sync_logs", indexes = {
    @Index(name = "idx_csl_started", columnList = "started_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @CreatedDate
    @Column(name = "started_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private SyncStatus status = SyncStatus.SUCCESS;

    @Column(name = "records_updated")
    @Builder.Default
    private Integer recordsUpdated = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(length = 50)
    @Builder.Default
    private String source = "MOCK_COURT_SYNC";
}
