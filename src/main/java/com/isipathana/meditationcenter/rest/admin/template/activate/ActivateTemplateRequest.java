package com.isipathana.meditationcenter.rest.admin.template.activate;

/**
 * Request DTO for activating a template.
 *
 * @author Sathira Basnayake
 */
public record ActivateTemplateRequest(
        Long templateId
) {
    public ActivateTemplateRequest {
        if (templateId == null || templateId <= 0) {
            throw new IllegalArgumentException("Template ID must be a positive number");
        }
    }
}
