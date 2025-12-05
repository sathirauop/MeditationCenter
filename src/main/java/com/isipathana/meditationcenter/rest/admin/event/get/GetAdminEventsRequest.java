package com.isipathana.meditationcenter.rest.admin.event.get;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request DTO for GetAdminEvents endpoint.
 * Supports pagination with limit and offset parameters.
 *
 * @author Sathira Basnayake
 */
public record GetAdminEventsRequest(
        @Min(1) @Max(100) int limit,
        @Min(0) int offset
) {
    /**
     * Default constructor with default pagination values.
     * Default: 20 items per page, starting from offset 0.
     */
    public GetAdminEventsRequest() {
        this(20, 0);
    }
}
