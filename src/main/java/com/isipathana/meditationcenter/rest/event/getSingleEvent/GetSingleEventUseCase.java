package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for retrieving a single event by ID.
 * Handles business logic for fetching an active event with presigned image URLs.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class GetSingleEventUseCase implements UseCase<GetSingleEventRequest, GetSingleEventResponse> {

    private final GetSingleEventDataAccess repository;
    private final GetSingleEventResponseBuilder responseBuilder;

    /**
     * Handles get single event operation.
     * Retrieves an active event by ID and uses the presenter to build the response.
     *
     * @param request Request containing the event ID
     * @return Response containing event details with presigned image URLs
     * @throws ResourceNotFoundException if event is not found or not active
     */
    @Override
    @Transactional(readOnly = true)
    public GetSingleEventResponse handle(GetSingleEventRequest request) {
        // Fetch event from repository
        Event event = repository.findActiveEventById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", request.eventId()));

        // Use presenter to build response with presigned URLs
        return responseBuilder.build(event);
    }
}
