package com.isipathana.meditationcenter.rest.admin.activity.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating an activity.
 *
 * @author Sathira Basnayake
 */
public record PostActivityRequest(
        @NotBlank(message = "Activity title is required")
        @Size(min = 3, max = 255, message = "Activity title must be between 3 and 255 characters")
        String title,
        
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,
        
        @Size(max = 255, message = "Media URL must not exceed 255 characters")
        String mediaUrl
) {}
