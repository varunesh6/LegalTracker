package com.legaltrack.entity;

import com.legaltrack.enums.Severity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_attention", indexes = {
    @Index(name = "idx_cat_resolved", columnList = "resolved"),
    @Index(name = "idx_cat_severity", columnList = "severity")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseAttention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private Severity severity = Severity.WARNING;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean resolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
