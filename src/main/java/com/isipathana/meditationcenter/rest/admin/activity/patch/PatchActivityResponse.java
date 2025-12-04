package com.isipathana.meditationcenter.rest.admin.activity.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response DTO for updated activity.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PatchActivityResponse(
        @JsonProperty("activity_id")
        Long activityId,
        
        String title,
        String description,
        
        @JsonProperty("media_url")
        String mediaUrl,
        
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) implements ApiResponse {}
