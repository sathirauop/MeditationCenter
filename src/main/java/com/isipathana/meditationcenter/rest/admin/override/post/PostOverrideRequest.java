package com.isipathana.meditationcenter.rest.admin.override.post;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a schedule override for a specific date.
 *
 * @author Sathira Basnayake
 */
public record PostOverrideRequest(
        @NotNull(message = "Override date is required")
        LocalDate overrideDate,

        @Size(max = 500, message = "Reason cannot exceed 500 characters")

        @NotEmpty(message = "At least one activity is required")
        @Valid
        List<OverrideActivityDto> activities
) {}
