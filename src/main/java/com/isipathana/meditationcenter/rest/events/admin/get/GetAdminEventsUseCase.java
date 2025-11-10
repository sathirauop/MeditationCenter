package com.isipathana.meditationcenter.rest.events.admin.get;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for GetAdminEvents endpoint.
 * Handles business logic for retrieving events for admin users.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class GetAdminEventsUseCase implements UseCase<GetAdminEventsRequest, OffsetSearchResponse<GetAdminEventsResponse>> {

    private final GetAdminEventsDataAccess repository;
    private final GetAdminEventsResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetAdminEventsResponse> handle(GetAdminEventsRequest request) {
        // Calculate actual offset (offset * limit)
        int actualOffset = request.offset() * request.limit();

        // Fetch events from repository
        List<Event> events = repository.findActiveEvents(actualOffset, request.limit());

        // Get total count
        long totalCount = repository.getActiveEventCount();

        // Build response using presenter
        return responseBuilder.build(events.stream(), actualOffset, totalCount);
    }
}
