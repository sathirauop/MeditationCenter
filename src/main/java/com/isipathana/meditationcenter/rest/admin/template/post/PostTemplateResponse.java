package com.isipathana.meditationcenter.rest.admin.template.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for template creation.
 *
 * @author Sathira Basnayake
 */
public record PostTemplateResponse(
        @JsonProperty("template_id")
        Long templateId,

        String name,

        String description,

        @JsonProperty("is_active")
        Boolean isActive,

        List<TemplateActivityResponse> activities,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {
    public record TemplateActivityResponse(
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

            String notes
    ) {}
}
