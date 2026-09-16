package com.legaltrack.entity;

import com.legaltrack.enums.LegalAidCategory;
import com.legaltrack.enums.LegalAidStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "legal_aid_applications", indexes = {
    @Index(name = "idx_laa_status", columnList = "status"),
    @Index(name = "idx_laa_number", columnList = "application_number")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalAidApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_number", nullable = false, unique = true, length = 50)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "case_type", nullable = false, length = 100)
    private String caseType;

    @Column(name = "case_stage", length = 100)
    private String caseStage;

    @Column(name = "matter_description", nullable = false, columnDefinition = "TEXT")
    private String matterDescription;

    @Column(name = "opponent_information", columnDefinition = "TEXT")
    private String opponentInformation;

    @Column(name = "annual_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal annualIncome;

    @Column(name = "employment_status", nullable = false, length = 100)
    private String employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_category", nullable = false, length = 100)
    private LegalAidCategory selectedCategory;

    @Column(name = "supporting_information", columnDefinition = "TEXT")
    private String supportingInformation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private LegalAidStatus status = LegalAidStatus.SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_lawyer_id")
    private User assignedLawyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_case_id")
    private CaseFile createdCase;

    @Version
    private Long version;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LegalAidDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LegalAidStatusHistory> statusHistory = new ArrayList<>();

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private LegalAidAssignment assignment;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
