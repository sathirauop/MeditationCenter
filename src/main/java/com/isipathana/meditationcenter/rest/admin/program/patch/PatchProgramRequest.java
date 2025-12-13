package com.isipathana.meditationcenter.rest.admin.program.patch;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a meditation program.
 * All fields are optional for partial updates.
 *
 * @author Sathira Basnayake
 */
public record PatchProgramRequest(
        @Size(min = 3, max = 255, message = "Program name must be between 3 and 255 characters")
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @Min(value = 0, message = "Max seats must be at least 0")
        Integer maxSeats,

        Boolean isActive
) {}
