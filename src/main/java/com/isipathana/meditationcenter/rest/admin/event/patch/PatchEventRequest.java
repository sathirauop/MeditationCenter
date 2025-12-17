package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for updating an event (partial update).
 * All fields are optional - only provided fields will be updated.
 *
 * @author Sathira Basnayake
 */
public record PatchEventRequest(
        @Size(min = 3, max = 255, message = "Event name must be between 3 and 255 characters")
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @Size(min = 3, max = 255, message = "Sinhala name must be between 3 and 255 characters")
        String nameSi,

        @Size(max = 5000, message = "Sinhala description must not exceed 5000 characters")
        String descriptionSi,

        @Future(message = "Event date must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate eventDate,

        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        Boolean isActive
) {
}
