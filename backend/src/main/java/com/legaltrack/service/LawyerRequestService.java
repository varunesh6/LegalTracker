package com.legaltrack.service;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.lawyer.CreateLawyerRequestDto;
import com.legaltrack.dto.lawyer.LawyerRequestDto;
import org.springframework.data.domain.Pageable;

public interface LawyerRequestService {
    LawyerRequestDto sendRequest(CreateLawyerRequestDto request);
    LawyerRequestDto acceptRequest(Long requestId);
    LawyerRequestDto rejectRequest(Long requestId);
    PagedResponse<LawyerRequestDto> getMyRequests(Pageable pageable);
}
