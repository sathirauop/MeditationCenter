package com.isipathana.meditationcenter.rest.admin.template.activities.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * Request DTO for adding an activity to a template.
 *
 * @author Sathira Basnayake
 */
public record PostTemplateActivityRequest(
        Long templateId,

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
