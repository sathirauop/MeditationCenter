package com.isipathana.meditationcenter.rest.admin.override.get;

import jakarta.validation.constraints.Min;

import java.time.LocalDate;

/**
 * Request DTO for getting schedule overrides with optional date filtering.
 *
 * @author Sathira Basnayake
 */
public record GetOverridesRequest(
        @Min(value = 1, message = "Page must be at least 1")
        int page,

        @Min(value = 1, message = "Limit must be at least 1")
        int limit,

        LocalDate fromDate,

        LocalDate toDate
) {}
