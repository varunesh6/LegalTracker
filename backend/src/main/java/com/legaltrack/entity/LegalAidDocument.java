package com.legaltrack.entity;

import com.legaltrack.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "legal_aid_documents")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalAidDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LegalAidApplication application;

    @Column(name = "document_name", nullable = false, length = 150)
    private String documentName;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(name = "storage_key", nullable = false, length = 255)
    private String storageKey;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 50)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @CreatedDate
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}
