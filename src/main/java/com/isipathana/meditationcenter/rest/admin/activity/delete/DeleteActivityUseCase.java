package com.isipathana.meditationcenter.rest.admin.activity.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UseCase for deleting activities.
 * Handles business logic for activity deletion.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteActivityUseCase {
    
    private final DeleteActivityDataAccess repository;
    
    @Transactional
    public DeleteActivityResponse execute(Long activityId) {
        log.info("Starting deletion for activity: {}", activityId);
        
        // Verify existence
        Optional<Activity> activityOptional = repository.findActivityById(activityId);
        
        if (activityOptional.isEmpty()) {
            log.warn("Activity not found: {}", activityId);
            return DeleteActivityResponse.notFound(activityId);
        }
        
        Activity activity = activityOptional.get();
        
        // Delete from database
        boolean dbSuccess = repository.deleteActivity(activityId);
        
        if (!dbSuccess) {
            log.error("Failed to delete activity from database");
            throw new RuntimeException("Failed to delete activity from database");
        }
        
        log.info("Successfully deleted activity {}", activityId);
        return DeleteActivityResponse.success(activityId, activity.title());
    }
}
