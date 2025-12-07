package com.isipathana.meditationcenter.rest.admin.override.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for override creation.
 *
 * @author Sathira Basnayake
 */
public record PostOverrideResponse(
        @JsonProperty("override_id")
        Long overrideId,

        @JsonProperty("override_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate overrideDate,


        List<OverrideActivityResponse> activities,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {
    public record OverrideActivityResponse(
            @JsonProperty("activity_id")
            Long activityId,

            @JsonProperty("activity_title")
            String activityTitle,

            @JsonProperty("start_time")
            @JsonFormat(pattern = "HH:mm")
            String startTime,

            @JsonProperty("end_time")
            @JsonFormat(pattern = "HH:mm")
            String endTime,

            String notes
    ) {}
}
