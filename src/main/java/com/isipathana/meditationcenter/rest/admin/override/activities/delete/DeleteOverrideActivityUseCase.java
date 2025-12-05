package com.isipathana.meditationcenter.rest.admin.override.activities.delete;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for deleting activity from override.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteOverrideActivityUseCase {

    private final DeleteOverrideActivityDataAccess repository;

    @Transactional
    public DeleteOverrideActivityResponse execute(Long overrideId, Long overrideActivityId) {
        log.info("Deleting override activity {} from override {}", overrideActivityId, overrideId);

        OverrideActivity overrideActivity = repository.findOverrideActivityById(overrideId, overrideActivityId)
                .orElseThrow(() -> new ResourceNotFoundException("Override activity not found"));

        Activity activity = repository.findActivityById(overrideActivity.activityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        repository.deleteOverrideActivity(overrideActivityId);

        log.info("Successfully deleted activity '{}' from override", activity.title());

        return new DeleteOverrideActivityResponse(
                true,
                String.format("Activity '%s' removed from override successfully", activity.title()),
                overrideActivityId,
                overrideId,
                activity.title()
        );
    }
}
