package com.isipathana.meditationcenter.rest.admin.activity.getSingle;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for retrieving a single activity by ID.
 * Handles business logic for fetching activity details.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetSingleActivityUseCase implements UseCase<GetSingleActivityRequest, GetSingleActivityResponse> {
    
    private final GetSingleActivityDataAccess repository;
    private final GetSingleActivityResponseBuilder responseBuilder;
    
    @Override
    @Transactional(readOnly = true)
    public GetSingleActivityResponse handle(GetSingleActivityRequest request) {
        log.info("Fetching activity with ID: {}", request.activityId());
        
        // Fetch activity
        Activity activity = repository.findActivityById(request.activityId())
                .orElseThrow(() -> {
                    log.warn("Activity not found with ID: {}", request.activityId());
                    return new ResourceNotFoundException("Activity not found with ID: " + request.activityId());
                });
        
        log.info("Activity found: {}", activity.title());
        
        // Use presenter to build response
        return responseBuilder.build(activity);
    }
}
