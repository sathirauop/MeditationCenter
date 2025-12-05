package com.isipathana.meditationcenter.rest.admin.activity.patch;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating activities.
 * Handles business logic for partial activity updates (PATCH).
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchActivityUseCase implements UseCase<PatchActivityRequest, PatchActivityResponse> {
    
    private final PatchActivityDataAccess repository;
    private final PatchActivityResponseBuilder responseBuilder;
    
    @Override
    @Transactional
    public PatchActivityResponse handle(PatchActivityRequest request) {
        log.info("Updating activity with ID: {}", request.activityId());
        
        // Fetch existing activity
        Activity existingActivity = repository.findActivityById(request.activityId())
                .orElseThrow(() -> {
                    log.warn("Activity not found with ID: {}", request.activityId());
                    return new ResourceNotFoundException("Activity not found with ID: " + request.activityId());
                });
        
        // Apply partial updates (only update non-null fields)
        Activity updatedActivity = Activity.builder()
                .activityId(existingActivity.activityId())
                .title(request.title() != null ? request.title() : existingActivity.title())
                .description(request.description() != null ? request.description() : existingActivity.description())
                .mediaUrl(request.mediaUrl() != null ? request.mediaUrl() : existingActivity.mediaUrl())
                .createdAt(existingActivity.createdAt())
                .build();
        
        // Update activity
        Activity savedActivity = repository.updateActivity(updatedActivity);
        
        log.info("Activity updated successfully: {}", savedActivity.title());
        
        // Use presenter to build response
        return responseBuilder.build(savedActivity);
    }
}
