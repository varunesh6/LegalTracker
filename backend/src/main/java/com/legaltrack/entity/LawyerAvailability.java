package com.legaltrack.entity;

import com.legaltrack.enums.LawyerAvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lawyer_availability")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawyerAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id", referencedColumnName = "id", nullable = false, unique = true)
    private LawyerProfile lawyer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private LawyerAvailabilityStatus status = LawyerAvailabilityStatus.ACCEPTING_CLIENTS;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    @Column(name = "available_until")
    private LocalDate availableUntil;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
