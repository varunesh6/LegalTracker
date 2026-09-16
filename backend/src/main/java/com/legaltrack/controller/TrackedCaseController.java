package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.tracked.TrackCaseRequest;
import com.legaltrack.dto.tracked.TrackedCaseDto;
import com.legaltrack.dto.tracked.UpdateTrackedCaseRequest;
import com.legaltrack.service.TrackedCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracked-cases")
@RequiredArgsConstructor
@Tag(name = "Saved & Tracked Cases", description = "User saved cases workspace with personalized nicknames and notification toggles")
public class TrackedCaseController {

    private final TrackedCaseService trackedCaseService;

    @PostMapping
    @Operation(summary = "Track and save a case to user portfolio")
    public ResponseEntity<ApiResponse<TrackedCaseDto>> trackCase(@Valid @RequestBody TrackCaseRequest request) {
        TrackedCaseDto response = trackedCaseService.trackCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Case added to tracked cases", response));
    }

    @GetMapping
    @Operation(summary = "Get paginated tracked cases with search")
    public ResponseEntity<ApiResponse<PagedResponse<TrackedCaseDto>>> getMyTrackedCases(
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        PagedResponse<TrackedCaseDto> response = trackedCaseService.getMyTrackedCases(query, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Tracked cases retrieved", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all tracked cases for current user")
    public ResponseEntity<ApiResponse<List<TrackedCaseDto>>> getAllTrackedCases() {
        List<TrackedCaseDto> list = trackedCaseService.getAllMyTrackedCases();
        return ResponseEntity.ok(ApiResponse.ok("All tracked cases retrieved", list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update nickname or notification settings for a tracked case")
    public ResponseEntity<ApiResponse<TrackedCaseDto>> updateTrackedCase(
            @PathVariable Long id,
            @RequestBody UpdateTrackedCaseRequest request
    ) {
        TrackedCaseDto updated = trackedCaseService.updateTrackedCase(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Tracked case updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Stop tracking a case")
    public ResponseEntity<ApiResponse<Void>> stopTracking(@PathVariable Long id) {
        trackedCaseService.stopTracking(id);
        return ResponseEntity.ok(ApiResponse.ok("Stopped tracking case"));
    }

    @PatchMapping("/{id}/notifications")
    @Operation(summary = "Toggle notification updates for a tracked case")
    public ResponseEntity<ApiResponse<Void>> toggleNotifications(
            @PathVariable Long id,
            @RequestParam boolean enabled
    ) {
        trackedCaseService.toggleNotifications(id, enabled);
        return ResponseEntity.ok(ApiResponse.ok("Notification setting updated"));
    }
}
