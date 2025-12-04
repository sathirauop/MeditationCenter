package com.isipathana.meditationcenter.rest.admin.template.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * DTO for activity within a template during creation.
 *
 * @author Sathira Basnayake
 */
public record TemplateActivityDto(
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
