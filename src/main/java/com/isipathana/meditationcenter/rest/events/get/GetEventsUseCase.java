package com.isipathana.meditationcenter.rest.events.get;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for retrieving events with pagination.
 * Handles business logic for fetching active events using the presenter pattern.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class GetEventsUseCase implements UseCase<GetEventsRequest, OffsetSearchResponse<GetEventsResponse>> {

    private final GetEventsDataAccess repository;
    private final GetEventResponseBuilder responseBuilder;

    /**
     * Handles get events operation with pagination.
     * Retrieves active events ordered by event date and start time.
     *
     * @param request Request containing pagination parameters (limit and offset)
     * @return Paginated response containing list of events
     */
    @Override
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetEventsResponse> handle(GetEventsRequest request) {
        // Calculate actual offset from page-based offset
        int actualOffset = request.offset() * request.limit();

        // Fetch events with pagination
        List<Event> events = repository.findActiveEvents(actualOffset, request.limit());

        // Get total count for pagination metadata
        long totalCount = repository.getActiveEventCount();

        // Use presenter to build response
        return responseBuilder.build(
                events.stream(),
                actualOffset,
                totalCount
        );
    }
}
