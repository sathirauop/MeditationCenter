package com.isipathana.meditationcenter.rest.admin.template.getSingle;

/**
 * Request DTO for retrieving a single template by ID.
 *
 * @author Sathira Basnayake
 */
public record GetSingleTemplateRequest(
        Long templateId
) {
    public GetSingleTemplateRequest {
        if (templateId == null || templateId <= 0) {
            throw new IllegalArgumentException("Template ID must be a positive number");
        }
    }
}
