package com.isipathana.meditationcenter.rest.schedule.get;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request DTO for getting schedule for a specific date.
 *
 * @author Sathira Basnayake
 */
public record GetScheduleRequest(
        @NotNull(message = "Date is required")
        LocalDate date
) {}
