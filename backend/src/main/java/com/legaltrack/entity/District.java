package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "districts", uniqueConstraints = {
    @UniqueConstraint(name = "uq_state_district", columnNames = {"state_id", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String code;
}
