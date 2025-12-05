package com.isipathana.meditationcenter.rest.admin.override.put;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for updating an override.
 *
 * @author Sathira Basnayake
 */
public record PutOverrideResponse(
        @JsonProperty("override_id")
        Long overrideId,

        @JsonProperty("override_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate overrideDate,


        List<ActivityResponse> activities,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
    public record ActivityResponse(
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
