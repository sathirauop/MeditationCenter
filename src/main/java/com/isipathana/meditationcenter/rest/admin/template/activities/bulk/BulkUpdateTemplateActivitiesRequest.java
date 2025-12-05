package com.isipathana.meditationcenter.rest.admin.template.activities.bulk;

import com.isipathana.meditationcenter.rest.admin.template.post.TemplateActivityDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for bulk updating template activities.
 * Replaces all existing activities with the provided list.
 *
 * @author Sathira Basnayake
 */
public record BulkUpdateTemplateActivitiesRequest(
        Long templateId,

        @NotEmpty(message = "At least one activity is required")
        @Valid
        List<TemplateActivityDto> activities
) {}
