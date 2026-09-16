package com.legaltrack.dto.casefile;

import com.legaltrack.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseOrderDto {
    private Long id;
    private Long caseId;
    private LocalDate orderDate;
    private String title;
    private OrderType orderType;
    private Long documentId;
    private String summary;
    private String source;
    private LocalDateTime createdAt;
}
