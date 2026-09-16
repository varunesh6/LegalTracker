package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_complex_id", nullable = false)
    private CourtComplex courtComplex;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "court_type", length = 100)
    private String courtType;

    @Column(name = "judge_designation", length = 150)
    private String judgeDesignation;
}
