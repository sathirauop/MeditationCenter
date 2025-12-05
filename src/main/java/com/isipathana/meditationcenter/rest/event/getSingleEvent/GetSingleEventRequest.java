package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for GET /api/event/{id} endpoint.
 * Contains the event ID to retrieve.
 *
 * @param eventId The ID of the event to retrieve (must be positive)
 * @author Sathira Basnayake
 */
public record GetSingleEventRequest(
        @NotNull @Positive Long eventId
) {}
