package com.isipathana.meditationcenter.rest.admin.override.get;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for a single override in the list.
 *
 * @author Sathira Basnayake
 */
public record GetOverridesResponse(
        @JsonProperty("override_id")
        Long overrideId,

        @JsonProperty("override_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate overrideDate,


        @JsonProperty("activity_count")
        Integer activityCount,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) implements ApiResponse {}
