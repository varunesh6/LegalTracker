package com.legaltrack.entity;

import com.legaltrack.enums.OrderType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_orders", indexes = {
    @Index(name = "idx_co_date", columnList = "order_date")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 100)
    @Builder.Default
    private OrderType orderType = OrderType.INTERIM_ORDER;

    @Column(name = "document_id")
    private Long documentId;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(length = 50)
    @Builder.Default
    private String source = "MOCK_COURT_SYNC";

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
