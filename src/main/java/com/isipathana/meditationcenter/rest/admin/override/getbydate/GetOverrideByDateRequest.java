package com.isipathana.meditationcenter.rest.admin.override.getbydate;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request DTO for getting override by date.
 *
 * @author Sathira Basnayake
 */
public record GetOverrideByDateRequest(
        @NotNull(message = "Date is required")
        LocalDate date
) {}
