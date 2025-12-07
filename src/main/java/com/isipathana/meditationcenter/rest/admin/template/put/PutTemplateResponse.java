package com.isipathana.meditationcenter.rest.admin.template.put;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for template update.
 *
 * @author Sathira Basnayake
 */
public record PutTemplateResponse(
        @JsonProperty("template_id")
        Long templateId,

        String name,

        String description,

        @JsonProperty("is_active")
        Boolean isActive,

        List<TemplateActivityResponse> activities,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
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
