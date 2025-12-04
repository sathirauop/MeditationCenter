package com.isipathana.meditationcenter.rest.admin.activity.getSingle;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for retrieving a single activity by ID.
 *
 * @author Sathira Basnayake
 */
public record GetSingleActivityRequest(
        @NotNull(message = "Activity ID is required")
        Long activityId
) {}
