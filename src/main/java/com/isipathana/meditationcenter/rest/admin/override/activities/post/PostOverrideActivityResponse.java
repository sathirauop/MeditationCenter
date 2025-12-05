package com.isipathana.meditationcenter.rest.admin.override.activities.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for adding activity to override.
 *
 * @author Sathira Basnayake
 */
public record PostOverrideActivityResponse(
        @JsonProperty("override_activity_id")
        Long overrideActivityId,

        @JsonProperty("override_id")
        Long overrideId,

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

        String notes,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {}
