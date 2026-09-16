package com.legaltrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lawyer_languages", uniqueConstraints = {
    @UniqueConstraint(name = "uq_lawyer_lang", columnNames = {"lawyer_id", "language"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawyerLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id", nullable = false)
    private LawyerProfile lawyer;

    @Column(nullable = false, length = 50)
    private String language;
}
