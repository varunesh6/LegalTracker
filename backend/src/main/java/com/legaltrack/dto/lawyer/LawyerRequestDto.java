package com.legaltrack.dto.lawyer;

import com.legaltrack.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawyerRequestDto {

    private Long id;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientMobile;
    private Long lawyerId;
    private String lawyerName;
    private String caseType;
    private Long courtId;
    private String courtName;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
