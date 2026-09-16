package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lawyer_courts", uniqueConstraints = {
    @UniqueConstraint(name = "uq_lawyer_court", columnNames = {"lawyer_id", "court_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawyerCourt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id", nullable = false)
    private LawyerProfile lawyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;
}
