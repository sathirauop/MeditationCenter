package com.isipathana.meditationcenter.rest.admin.override.put;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.rest.admin.override.post.OverrideActivityDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UseCase for updating an override.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PutOverrideUseCase implements UseCase<PutOverrideRequest, PutOverrideResponse> {

    private final PutOverrideDataAccess repository;
    private final PutOverrideResponseBuilder responseBuilder;

    @Transactional
    @Override
    public PutOverrideResponse handle(PutOverrideRequest request) {
        log.info("Updating override ID: {}", request.overrideId());

        // Verify override exists
        ScheduleOverride existingOverride = repository.findOverrideById(request.overrideId())
                .orElseThrow(() -> new ResourceNotFoundException("Override not found"));

        // Validate activities
        Map<Long, Activity> activityMap = validateActivities(request.activities());

        // Update override
        ScheduleOverride updatedOverride = ScheduleOverride.builder()
                .overrideId(request.overrideId())
                .overrideDate(request.overrideDate())
                .build();

        updatedOverride = repository.updateOverride(updatedOverride);

        // Delete all existing activities
        int deletedCount = repository.deleteAllOverrideActivities(request.overrideId());
        log.debug("Deleted {} existing activities", deletedCount);

        // Create new activities
        List<OverrideActivity> overrideActivities = new ArrayList<>();
        for (OverrideActivityDto activityDto : request.activities()) {
            OverrideActivity overrideActivity = OverrideActivity.builder()
                    .overrideId(updatedOverride.overrideId())
                    .activityId(activityDto.activityId())
                    .startTime(activityDto.startTime())
                    .endTime(activityDto.endTime())
                    .notes(activityDto.notes())
                    .build();

            OverrideActivity created = repository.createOverrideActivity(overrideActivity);
            overrideActivities.add(created);
        }

        log.info("Updated override with {} activities", overrideActivities.size());

        return responseBuilder.build(updatedOverride, overrideActivities, activityMap);
    }

    private Map<Long, Activity> validateActivities(List<OverrideActivityDto> activities) {
        List<Long> activityIds = activities.stream()
                .map(OverrideActivityDto::activityId)
                .distinct()
                .toList();

        List<Activity> foundActivities = repository.findActivitiesByIds(activityIds);

        if (foundActivities.size() != activityIds.size()) {
            throw new ResourceNotFoundException("One or more activities not found");
        }

        return foundActivities.stream()
                .collect(Collectors.toMap(Activity::activityId, activity -> activity));
    }
}
