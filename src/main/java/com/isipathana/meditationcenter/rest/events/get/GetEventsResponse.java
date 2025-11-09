package com.isipathana.meditationcenter.rest.events.get;

import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO for GET /api/events endpoint.
 * Contains essential event information for a single event.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetEventsResponse(
        Long eventId,
        String name,
        String description,
        LocalDate eventDate,
        LocalTime startTime,
        LocalTime endTime,
        String location,
        String images
) implements ApiResponse {}
