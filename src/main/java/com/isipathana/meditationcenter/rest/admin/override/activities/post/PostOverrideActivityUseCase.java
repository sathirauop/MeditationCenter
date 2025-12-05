package com.isipathana.meditationcenter.rest.admin.override.activities.post;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import com.isipathana.meditationcenter.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for adding activity to an override.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostOverrideActivityUseCase implements UseCase<PostOverrideActivityRequest, PostOverrideActivityResponse> {

    private final PostOverrideActivityDataAccess repository;
    private final PostOverrideActivityResponseBuilder responseBuilder;

    @Transactional
    @Override
    public PostOverrideActivityResponse handle(PostOverrideActivityRequest request) {
        log.info("Adding activity {} to override {}", request.activityId(), request.overrideId());

        // Verify override exists
        ScheduleOverride override = repository.findOverrideById(request.overrideId())
                .orElseThrow(() -> new ResourceNotFoundException("Override not found"));

        // Verify activity exists
        Activity activity = repository.findActivityById(request.activityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        // Create override activity
        OverrideActivity overrideActivity = OverrideActivity.builder()
                .overrideId(override.overrideId())
                .activityId(activity.activityId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .notes(request.notes())
                .build();

        OverrideActivity created = repository.createOverrideActivity(overrideActivity);

        return responseBuilder.build(created, activity);
    }
}
