package com.isipathana.meditationcenter.rest.admin.template.activities.delete;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for deleting a template activity.
 *
 * @author Sathira Basnayake
 */
public record DeleteTemplateActivityResponse(
        boolean success,

        String message,

        @JsonProperty("template_activity_id")
        Long templateActivityId,

        @JsonProperty("template_id")
        Long templateId,

        @JsonProperty("activity_title")
        String activityTitle
) {
    public static DeleteTemplateActivityResponse success(
            Long templateActivityId,
            Long templateId,
            String activityTitle) {
        return new DeleteTemplateActivityResponse(
                true,
                String.format("Activity '%s' removed from template successfully", activityTitle),
                templateActivityId,
                templateId,
                activityTitle
        );
    }

    public static DeleteTemplateActivityResponse notFound(Long templateActivityId, Long templateId) {
        return new DeleteTemplateActivityResponse(
                false,
                String.format("Template activity not found with ID: %d in template: %d",
                        templateActivityId, templateId),
                templateActivityId,
                templateId,
                null
        );
    }
}
