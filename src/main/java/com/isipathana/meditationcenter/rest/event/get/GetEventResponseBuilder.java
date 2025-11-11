package com.isipathana.meditationcenter.rest.event.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;

import java.util.stream.Stream;

/**
 * Builder interface for creating GetEvents response.
 * Transforms a stream of Event domain objects into an OffsetSearchResponse.
 *
 * @author Sathira Basnayake
 */
public interface GetEventResponseBuilder {

    /**
     * Build paginated response from event stream.
     *
     * @param events        Stream of Event domain objects
     * @param currentOffset Current offset (starting position)
     * @param maxOffset     Maximum offset (total count)
     * @return Paginated response containing list of GetEventsResponse
     */
    OffsetSearchResponse<GetEventsResponse> build(
            Stream<Event> events,
            long currentOffset,
            long maxOffset
    );
}
