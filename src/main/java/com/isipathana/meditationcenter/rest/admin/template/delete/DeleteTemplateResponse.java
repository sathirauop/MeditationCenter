package com.isipathana.meditationcenter.rest.admin.template.delete;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for template deletion.
 *
 * @author Sathira Basnayake
 */
public record DeleteTemplateResponse(
        boolean success,

        String message,

        @JsonProperty("template_id")
        Long templateId,

        @JsonProperty("template_name")
        String templateName
) {
    public static DeleteTemplateResponse success(Long templateId, String templateName) {
        return new DeleteTemplateResponse(
                true,
                String.format("Template '%s' (ID: %d) deleted successfully", templateName, templateId),
                templateId,
                templateName
        );
    }

    public static DeleteTemplateResponse notFound(Long templateId) {
        return new DeleteTemplateResponse(
                false,
                String.format("Template not found with ID: %d", templateId),
                templateId,
                null
        );
    }
}
