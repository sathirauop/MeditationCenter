package com.isipathana.meditationcenter.rest.admin.template.activities.put;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response DTO for updating a template activity.
 *
 * @author Sathira Basnayake
 */
public record PutTemplateActivityResponse(
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

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {}
