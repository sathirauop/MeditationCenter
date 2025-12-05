package com.isipathana.meditationcenter.rest.admin.template.activities.put;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * Request DTO for updating a template activity.
 *
 * @author Sathira Basnayake
 */
public record PutTemplateActivityRequest(
        Long templateId,
        Long templateActivityId,

        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        String notes
) {}
