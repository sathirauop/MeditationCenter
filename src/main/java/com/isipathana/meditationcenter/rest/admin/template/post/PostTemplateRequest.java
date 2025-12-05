package com.isipathana.meditationcenter.rest.admin.template.post;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating a schedule template with activities.
 *
 * @author Sathira Basnayake
 */
public record PostTemplateRequest(
        @NotBlank(message = "Template name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotEmpty(message = "At least one activity is required")
        @Valid
        List<TemplateActivityDto> activities
) {}
