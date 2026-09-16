package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "case_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;
}
