package com.isipathana.meditationcenter.rest.admin.template.activate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for template activation.
 *
 * @author Sathira Basnayake
 */
public record ActivateTemplateResponse(
        @JsonProperty("template_id")
        Long templateId,

        String name,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("previous_active_template_id")
        Long previousActiveTemplateId,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt,

        String message
) {}
