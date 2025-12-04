package com.isipathana.meditationcenter.rest.admin.activity.get;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request DTO for retrieving paginated activities.
 *
 * @author Sathira Basnayake
 */
public record GetActivitiesRequest(
        @Min(1) @Max(100) int limit,
        @Min(0) int offset
) {
    /**
     * Default constructor with default pagination values.
     */
    public GetActivitiesRequest() {
        this(20, 0);
    }
}
