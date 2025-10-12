package com.isipathana.meditationcenter.rest.admin.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for creating a new event.
 *
 * @author Sathira Basnayake
 */
public record PostEventRequest(
        @NotBlank(message = "Event name is required")
        @Size(min = 3, max = 255, message = "Event name must be between 3 and 255 characters")
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @NotNull(message = "Event date is required")
        @Future(message = "Event date must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate eventDate,

        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        @Min(value = 1, message = "Maximum participants must be at least 1")
        Integer maxParticipants,

        String images,

        Boolean isActive,

        Boolean requiresRegistration
) {
}
