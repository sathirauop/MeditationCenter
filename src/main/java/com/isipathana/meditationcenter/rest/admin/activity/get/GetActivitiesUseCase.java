package com.isipathana.meditationcenter.rest.admin.activity.get;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for retrieving paginated list of activities.
 * Handles business logic for fetching and transforming activities.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetActivitiesUseCase implements UseCase<GetActivitiesRequest, OffsetSearchResponse<GetActivitiesResponse>> {

    private final GetActivitiesDataAccess repository;
    private final GetActivitiesResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetActivitiesResponse> handle(GetActivitiesRequest request) {
        log.info("Fetching activities with limit: {}, offset: {}", request.limit(), request.offset());

        // Calculate actual offset
        int actualOffset = request.offset() * request.limit();

        // Fetch activities and total count
        List<Activity> activities = repository.findActivities(actualOffset, request.limit());
        long totalCount = repository.getActivityCount();

        log.info("Found {} activities out of {} total", activities.size(), totalCount);

        // Use presenter to build response
        return responseBuilder.build(activities.stream(), actualOffset, totalCount);
    }
}
