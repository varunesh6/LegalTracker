package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lawyer_profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawyerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @Column(name = "bar_registration_number", nullable = false, unique = true, length = 100)
    private String barRegistrationNumber;

    @Column(name = "enrollment_year", nullable = false)
    private Integer enrollmentYear;

    @Column(name = "experience_years")
    @Builder.Default
    private Integer experienceYears = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "office_address", columnDefinition = "TEXT")
    private String officeAddress;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @OneToOne(mappedBy = "lawyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private LawyerAvailability availability;

    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<LawyerSpecialization> specializations = new HashSet<>();

    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<LawyerCourt> courts = new HashSet<>();

    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<LawyerLanguage> languages = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
