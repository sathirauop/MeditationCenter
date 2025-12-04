package com.isipathana.meditationcenter.rest.admin.activity.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for transforming Activity domain objects into GetActivitiesResponse DTOs.
 * Implements the ResponseBuilder interface to create paginated responses.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetActivitiesPresenter implements GetActivitiesResponseBuilder {
    
    private final OffsetSearchResponse.Factory responseFactory;
    
    @Override
    public OffsetSearchResponse<GetActivitiesResponse> build(
            Stream<Activity> activities,
            long currentOffset,
            long maxOffset) {
        
        List<GetActivitiesResponse> responseList = activities
                .map(this::mapActivityToResponse)
                .toList();
        
        return responseFactory.create(responseList, currentOffset, maxOffset);
    }
    
    /**
     * Maps an Activity domain object to GetActivitiesResponse DTO.
     */
    private GetActivitiesResponse mapActivityToResponse(Activity activity) {
        return GetActivitiesResponse.builder()
                .activityId(activity.activityId())
                .title(activity.title())
                .description(activity.description())
                .mediaUrl(activity.mediaUrl())
                .createdAt(activity.createdAt())
                .updatedAt(activity.updatedAt())
                .build();
    }
}
