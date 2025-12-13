package com.isipathana.meditationcenter.rest.admin.program.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a meditation program.
 *
 * @author Sathira Basnayake
 */
public record PostProgramRequest(
        @NotBlank(message = "Program name is required")
        @Size(min = 3, max = 255, message = "Program name must be between 3 and 255 characters")
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @Min(value = 0, message = "Max seats must be at least 0")
        Integer maxSeats,

        Boolean isActive
) {}
