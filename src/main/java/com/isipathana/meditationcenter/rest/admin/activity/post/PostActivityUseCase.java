package com.isipathana.meditationcenter.rest.admin.activity.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for creating new activities.
 * Handles business logic for activity creation.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostActivityUseCase {

    private final PostActivityDataAccess repository;
    private final PostActivityResponseBuilder responseBuilder;

    @Transactional
    public PostActivityResponse execute(PostActivityRequest request) {
        log.info("Creating new activity: {}", request.title());

        // Build activity domain object
        Activity activity = Activity.builder()
                .title(request.title())
                .description(request.description())
                .mediaUrl(request.mediaUrl())
                .build();

        // Create activity
        Activity createdActivity = repository.createActivity(activity);

        log.info("Activity created successfully with ID: {}", createdActivity.activityId());

        // Use presenter to build response
        return responseBuilder.build(createdActivity);
    }
}
