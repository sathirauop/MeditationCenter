package com.isipathana.meditationcenter.rest.event.get;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request DTO for GET /api/events endpoint.
 * Supports offset-based pagination for retrieving events.
 *
 * @param limit  Maximum number of events to return (1-100)
 * @param offset Page offset for pagination (0-based)
 * @author Sathira Basnayake
 */
public record GetEventsRequest(
        @Min(1) @Max(100) int limit,
        @Min(0) int offset
) {
    /**
     * Default constructor with sensible defaults.
     */
    public GetEventsRequest() {
        this(20, 0); // Default: 20 items, first page
    }
}
