package com.isipathana.meditationcenter.rest.admin.override.activities.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * Request DTO for adding activity to an override.
 *
 * @author Sathira Basnayake
 */
public record PostOverrideActivityRequest(
        Long overrideId,

        @NotNull(message = "Activity ID is required")
        Long activityId,

        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        String notes
) {}
