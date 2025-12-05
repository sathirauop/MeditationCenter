package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for single template retrieval with full details.
 *
 * @author Sathira Basnayake
 */
public record GetSingleTemplateResponse(
        @JsonProperty("template_id")
        Long templateId,

        String name,

        String description,

        @JsonProperty("is_active")
        Boolean isActive,

        List<TemplateActivityResponse> activities,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
    public record TemplateActivityResponse(
            @JsonProperty("template_activity_id")
            Long templateActivityId,

            @JsonProperty("activity_id")
            Long activityId,

            @JsonProperty("activity_title")
            String activityTitle,

            @JsonProperty("activity_description")
            String activityDescription,

            @JsonProperty("start_time")
            @JsonFormat(pattern = "HH:mm")
            LocalTime startTime,

            @JsonProperty("end_time")
            @JsonFormat(pattern = "HH:mm")
            LocalTime endTime,

            String notes
    ) {}
}
