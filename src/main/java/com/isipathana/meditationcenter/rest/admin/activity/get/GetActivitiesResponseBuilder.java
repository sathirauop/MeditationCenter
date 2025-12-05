package com.isipathana.meditationcenter.rest.admin.activity.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.schedule.Activity;

import java.util.stream.Stream;

/**
 * Builder interface for creating GetActivities response.
 * Transforms a stream of Activity domain objects into an OffsetSearchResponse.
 *
 * @author Sathira Basnayake
 */
public interface GetActivitiesResponseBuilder {
    
    /**
     * Build paginated response from activity stream.
     *
     * @param activities    Stream of Activity domain objects
     * @param currentOffset Current offset (starting position)
     * @param maxOffset     Maximum offset (total count)
     * @return Paginated response containing list of GetActivitiesResponse
     */
    OffsetSearchResponse<GetActivitiesResponse> build(
            Stream<Activity> activities,
            long currentOffset,
            long maxOffset
    );
}
