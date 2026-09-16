package com.legaltrack.entity;

import com.legaltrack.enums.HearingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_hearings", indexes = {
    @Index(name = "idx_ch_date", columnList = "hearing_date"),
    @Index(name = "idx_ch_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseHearing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "hearing_date", nullable = false)
    private LocalDate hearingDate;

    @Column(name = "hearing_time", length = 20)
    private String hearingTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Column(name = "judge_name", length = 150)
    private String judgeName;

    @Column(length = 255)
    private String purpose;

    @Column(length = 100)
    private String stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private HearingStatus status = HearingStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "next_hearing_date")
    private LocalDate nextHearingDate;

    @Column(length = 50)
    @Builder.Default
    private String source = "MOCK_COURT_SYNC";

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
