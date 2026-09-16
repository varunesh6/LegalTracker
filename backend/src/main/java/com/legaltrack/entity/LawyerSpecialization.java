package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lawyer_specializations", uniqueConstraints = {
    @UniqueConstraint(name = "uq_lawyer_spec", columnNames = {"lawyer_id", "specialization"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawyerSpecialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id", nullable = false)
    private LawyerProfile lawyer;

    @Column(nullable = false, length = 100)
    private String specialization;
}
