package com.legaltrack.entity;

import com.legaltrack.enums.CaseStage;
import com.legaltrack.enums.CaseStatus;
import com.legaltrack.enums.EngagementType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases", indexes = {
    @Index(name = "idx_case_cnr", columnList = "cnr_number"),
    @Index(name = "idx_case_number", columnList = "case_number"),
    @Index(name = "idx_case_filing", columnList = "filing_number"),
    @Index(name = "idx_case_fir", columnList = "fir_number"),
    @Index(name = "idx_case_status", columnList = "status"),
    @Index(name = "idx_case_next_hearing", columnList = "next_hearing_date")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "internal_reference_id", nullable = false, unique = true, length = 64)
    private String internalReferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id")
    private User lawyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_complex_id")
    private CourtComplex courtComplex;

    @Column(name = "case_type", nullable = false, length = 100)
    private String caseType;

    @Column(name = "case_category", length = 50)
    private String caseCategory;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "cnr_number", length = 50)
    private String cnrNumber;

    @Column(name = "case_number", length = 100)
    private String caseNumber;

    @Column(name = "filing_number", length = 100)
    private String filingNumber;

    @Column(name = "filing_date")
    private LocalDate filingDate;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "fir_number", length = 50)
    private String firNumber;

    @Column(name = "fir_year")
    private Integer firYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id")
    private PoliceStation policeStation;

    @Column(length = 255)
    private String act;

    @Column(length = 255)
    private String section;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private CaseStatus status = CaseStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    @Builder.Default
    private CaseStage stage = CaseStage.APPEARANCE;

    @Column(name = "matter_description", columnDefinition = "TEXT")
    private String matterDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "engagement_type", length = 50)
    @Builder.Default
    private EngagementType engagementType = EngagementType.PRIVATE_LAWYER;

    @Column(name = "is_demo_data")
    @Builder.Default
    private Boolean isDemoData = true;

    @Column(name = "next_hearing_date")
    private LocalDate nextHearingDate;

    @Version
    private Long version;

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseParty> parties = new ArrayList<>();

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseAdvocate> advocates = new ArrayList<>();

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseEvent> events = new ArrayList<>();

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseDiaryEntry> diaryEntries = new ArrayList<>();

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseHearing> hearings = new ArrayList<>();

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseOrder> orders = new ArrayList<>();

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseAttention> attentionItems = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
