package com.isipathana.meditationcenter.rest.admin.override.getbydate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for override by date with full activity details.
 *
 * @author Sathira Basnayake
 */
public record GetOverrideByDateResponse(
        @JsonProperty("override_id")
        Long overrideId,

        @JsonProperty("override_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate overrideDate,


        List<OverrideActivityDetail> activities,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
    public record OverrideActivityDetail(
            @JsonProperty("override_activity_id")
            Long overrideActivityId,

            @JsonProperty("activity_id")
            Long activityId,

            @JsonProperty("activity_title")
            String activityTitle,

            @JsonProperty("activity_description")
            String activityDescription,

            @JsonProperty("start_time")
            @JsonFormat(pattern = "HH:mm")
            String startTime,

            @JsonProperty("end_time")
            @JsonFormat(pattern = "HH:mm")
            String endTime,

            String notes
    ) {}
}
