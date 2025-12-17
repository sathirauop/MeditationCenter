package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating an event (partial update).
 * Handles business logic for PATCH /api/admin/event/{eventId}.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchEventUseCase implements UseCase<PatchEventRequest, PatchEventResponse> {

    private final PatchEventDataAccess repository;
    private final PatchEventResponseBuilder presenter;

    /**
     * Update event with partial data.
     * Only non-null fields in the request will be updated.
     *
     * @param eventId the ID of the event to update
     * @param request the update request with optional fields
     * @return the updated event
     */
    @Transactional
    public PatchEventResponse execute(Long eventId, PatchEventRequest request) {
        log.info("Updating event ID: {}", eventId);

        // Verify event exists
        Event existingEvent = repository.findEventById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found with ID: " + eventId
                ));

        log.debug("Found existing event: {}", existingEvent.name());

        // Build Event with only fields to update (non-null fields from request)
        Event eventToUpdate = Event.builder()
                .eventId(eventId)
                .name(request.name())
                .description(request.description())
                .nameSi(request.nameSi())
                .descriptionSi(request.descriptionSi())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .location(request.location())
                .isActive(request.isActive())
                .build();

        // Update event
        Event updatedEvent = repository.updateEvent(eventToUpdate);

        log.info("Successfully updated event ID: {}", eventId);

        // Build response using presenter
        return presenter.build(updatedEvent);
    }

    @Override
    public PatchEventResponse handle(PatchEventRequest request) {
        throw new UnsupportedOperationException(
                "Use execute(Long eventId, PatchEventRequest request) instead"
        );
    }
}
