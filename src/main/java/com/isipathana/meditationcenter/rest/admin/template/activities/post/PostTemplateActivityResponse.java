package com.isipathana.meditationcenter.rest.admin.template.activities.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response DTO for adding an activity to a template.
 *
 * @author Sathira Basnayake
 */
public record PostTemplateActivityResponse(
        @JsonProperty("template_activity_id")
        Long templateActivityId,

        @JsonProperty("template_id")
        Long templateId,

        @JsonProperty("activity_id")
        Long activityId,

        @JsonProperty("activity_title")
        String activityTitle,

        @JsonProperty("start_time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @JsonProperty("end_time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        String notes,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {}
