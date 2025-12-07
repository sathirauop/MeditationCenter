package com.isipathana.meditationcenter.rest.admin.override.delete;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Response DTO for deleting an override.
 *
 * @author Sathira Basnayake
 */
public record DeleteOverrideResponse(
        boolean success,

        String message,

        @JsonProperty("override_id")
        Long overrideId,

        @JsonProperty("override_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate overrideDate
) {}
