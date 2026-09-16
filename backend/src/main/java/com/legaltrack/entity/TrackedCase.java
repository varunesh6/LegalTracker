package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracked_cases", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_case_tracking", columnNames = {"user_id", "case_id"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackedCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(length = 150)
    private String nickname;

    @Column(name = "notifications_enabled", nullable = false)
    @Builder.Default
    private Boolean notificationsEnabled = true;

    @CreatedDate
    @Column(name = "tracked_at", updatable = false)
    private LocalDateTime trackedAt;

    @Column(name = "last_viewed_at")
    @Builder.Default
    private LocalDateTime lastViewedAt = LocalDateTime.now();
}
