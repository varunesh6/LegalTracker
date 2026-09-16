package com.legaltrack.dto.lawyer;

import com.legaltrack.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawyerVerificationDto {
    private Long id;
    private Long lawyerId;
    private String lawyerName;
    private String barRegistrationNumber;
    private String documentPath;
    private VerificationStatus verificationStatus;
    private Long verifiedById;
    private String verifiedByName;
    private LocalDateTime verifiedAt;
    private String remarks;
}
