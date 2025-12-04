package com.isipathana.meditationcenter.rest.admin.activity.patch;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an activity.
 * All fields are optional except activityId (for partial updates).
 *
 * @author Sathira Basnayake
 */
public record PatchActivityRequest(
        @NotNull(message = "Activity ID is required")
        Long activityId,
        
        @Size(min = 3, max = 255, message = "Activity title must be between 3 and 255 characters")
        String title,
        
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,
        
        @Size(max = 255, message = "Media URL must not exceed 255 characters")
        String mediaUrl
) {}
