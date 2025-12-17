package com.isipathana.meditationcenter.rest.admin.event.post;

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

        @Size(min = 3, max = 255, message = "Sinhala name must be between 3 and 255 characters")
        String nameSi,

        @Size(max = 5000, message = "Sinhala description must not exceed 5000 characters")
        String descriptionSi,

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

        Boolean isActive
) {
}
