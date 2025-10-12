package com.isipathana.meditationcenter.rest.admin.event;

import com.isipathana.meditationcenter.exception.ValidationException;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for creating a new event.
 * Handles business logic for event creation by admin users.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class PostEventUseCase {

    private final PostEventDataAccess repository;

    /**
     * Execute event creation.
     *
     * @param request Event creation request
     * @return Response with created event details
     * @throws ValidationException if end time is before start time
     */
    @Transactional
    public PostEventResponse execute(PostEventRequest request) {
        // Validate time range
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ValidationException("End time must be after start time");
        }

        // Build event entity
        Event event = Event.builder()
                .name(request.name())
                .description(request.description())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .location(request.location())
                .images(request.images())
                .isActive(request.isActive() != null ? request.isActive() : true)
                .build();

        // Create event in database
        Event createdEvent = repository.createEvent(event);

        // Map to response
        return new PostEventResponse(
                createdEvent.eventId(),
                createdEvent.name(),
                createdEvent.description(),
                createdEvent.eventDate(),
                createdEvent.startTime(),
                createdEvent.endTime(),
                createdEvent.location(),
                createdEvent.images(),
                createdEvent.isActive(),
                createdEvent.createdAt(),
                createdEvent.updatedAt()
        );
    }
}
