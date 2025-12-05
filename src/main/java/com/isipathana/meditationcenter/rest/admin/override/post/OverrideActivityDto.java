package com.isipathana.meditationcenter.rest.admin.override.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * DTO for activity within an override during creation.
 *
 * @author Sathira Basnayake
 */
public record OverrideActivityDto(
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
