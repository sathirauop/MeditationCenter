package com.isipathana.meditationcenter.rest.admin.event.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;

import java.util.stream.Stream;

/**
 * Response builder interface for GetAdminEvents endpoint.
 * Transforms Event domain objects to GetAdminEventsResponse DTOs.
 *
 * @author Sathira Basnayake
 */
public interface GetAdminEventsResponseBuilder {
    /**
     * Build paginated response from event stream.
     *
     * @param events        stream of events
     * @param currentOffset current offset in pagination
     * @param maxOffset     total count of events
     * @return paginated response
     */
    OffsetSearchResponse<GetAdminEventsResponse> build(
            Stream<Event> events,
            long currentOffset,
            long maxOffset
    );
}
