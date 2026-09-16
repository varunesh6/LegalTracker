package com.legaltrack.service;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.tracked.TrackCaseRequest;
import com.legaltrack.dto.tracked.TrackedCaseDto;
import com.legaltrack.dto.tracked.UpdateTrackedCaseRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackedCaseService {
    TrackedCaseDto trackCase(TrackCaseRequest request);
    PagedResponse<TrackedCaseDto> getMyTrackedCases(String query, Pageable pageable);
    List<TrackedCaseDto> getAllMyTrackedCases();
    TrackedCaseDto updateTrackedCase(Long id, UpdateTrackedCaseRequest request);
    void stopTracking(Long id);
    void toggleNotifications(Long id, boolean enabled);
}
