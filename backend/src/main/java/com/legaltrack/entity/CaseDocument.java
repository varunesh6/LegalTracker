package com.legaltrack.entity;

import com.legaltrack.enums.DocumentCategory;
import com.legaltrack.enums.DocumentVisibility;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_documents", indexes = {
    @Index(name = "idx_cd_category", columnList = "category")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentCategory category;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "storage_key", nullable = false, length = 255)
    private String storageKey;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(length = 64)
    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private DocumentVisibility visibility = DocumentVisibility.CLIENT_AND_LAWYER;

    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_document_id")
    private CaseDocument parentDocument;

    @CreatedDate
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}
