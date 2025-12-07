package com.isipathana.meditationcenter.rest.admin.template.get;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;

import java.time.LocalDateTime;

/**
 * Response DTO for template list item.
 *
 * @author Sathira Basnayake
 */
public record GetTemplatesResponse(
        @JsonProperty("template_id")
        Long templateId,

        String name,

        String description,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("activity_count")
        Integer activityCount,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) implements ApiResponse {}
