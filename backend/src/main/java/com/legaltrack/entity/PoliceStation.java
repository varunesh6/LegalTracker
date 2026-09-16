package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "police_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoliceStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50)
    private String code;
}
