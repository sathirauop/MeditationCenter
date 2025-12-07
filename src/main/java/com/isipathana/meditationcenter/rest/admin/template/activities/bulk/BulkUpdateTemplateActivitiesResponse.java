package com.isipathana.meditationcenter.rest.admin.template.activities.bulk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for bulk updating template activities.
 *
 * @author Sathira Basnayake
 */
public record BulkUpdateTemplateActivitiesResponse(
        @JsonProperty("template_id")
        Long templateId,

        @JsonProperty("template_name")
        String templateName,

        @JsonProperty("activities_count")
        int activitiesCount,

        List<TemplateActivityResponse> activities,

        String message
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
