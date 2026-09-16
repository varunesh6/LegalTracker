package com.legaltrack.entity;

import com.legaltrack.enums.LegalAidCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "legal_aid_eligibility_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalAidEligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @Builder.Default
    private String authority = "State Legal Services Authority (SLSA)";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private LegalAidCategory category;

    @Column(name = "income_limit", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal incomeLimit = new BigDecimal("300000.00");

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
