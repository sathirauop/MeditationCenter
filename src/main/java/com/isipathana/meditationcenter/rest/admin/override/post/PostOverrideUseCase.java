package com.isipathana.meditationcenter.rest.admin.override.post;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import com.isipathana.meditationcenter.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UseCase for creating a schedule override with activities.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostOverrideUseCase implements UseCase<PostOverrideRequest, PostOverrideResponse> {

    private final PostOverrideDataAccess repository;
    private final PostOverrideResponseBuilder responseBuilder;

    @Transactional
    @Override
    public PostOverrideResponse handle(PostOverrideRequest request) {
        log.info("Creating schedule override for date: {}", request.overrideDate());

        // Check if override already exists for this date
        if (repository.overrideExistsForDate(request.overrideDate())) {
            log.info("Override already exists for date: {}. Replacing with new override.", request.overrideDate());
            repository.deleteOverrideByDate(request.overrideDate());
        }

        // Validate that all activities exist
        Map<Long, Activity> activityMap = validateActivities(request.activities());

        // Create the override
        ScheduleOverride override = ScheduleOverride.builder()
                .overrideDate(request.overrideDate())
                .build();

        ScheduleOverride createdOverride = repository.createOverride(override);
        log.debug("Created override with ID: {}", createdOverride.overrideId());

        // Create override activities
        List<OverrideActivity> overrideActivities = new ArrayList<>();
        for (OverrideActivityDto activityDto : request.activities()) {
            OverrideActivity overrideActivity = OverrideActivity.builder()
                    .overrideId(createdOverride.overrideId())
                    .activityId(activityDto.activityId())
                    .startTime(activityDto.startTime())
                    .endTime(activityDto.endTime())
                    .notes(activityDto.notes())
                    .build();

            OverrideActivity created = repository.createOverrideActivity(overrideActivity);
            overrideActivities.add(created);
        }

        log.info("Created override with {} activities for date: {}",
                overrideActivities.size(), request.overrideDate());

        return responseBuilder.build(createdOverride, overrideActivities, activityMap);
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
