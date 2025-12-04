package com.isipathana.meditationcenter.rest.admin.template.activate;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UseCase for activating a schedule template.
 * Handles business logic for template activation including deactivation of other templates.
 * Important: Only ONE template can be active at a time.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivateTemplateUseCase implements UseCase<ActivateTemplateRequest, ActivateTemplateResponse> {

    private final ActivateTemplateDataAccess repository;
    private final ActivateTemplateResponseBuilder responseBuilder;

    @Transactional
    @Override
    public ActivateTemplateResponse handle(ActivateTemplateRequest request) {
        log.info("Activating template: {}", request.templateId());

        // Verify template exists
        ScheduleTemplate template = repository.findTemplateById(request.templateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Template not found with ID: " + request.templateId()));

        // Check if template is already active
        if (Boolean.TRUE.equals(template.isActive())) {
            log.info("Template {} is already active", request.templateId());
            return responseBuilder.build(template, null);
        }

        // Find currently active template (if any)
        Optional<ScheduleTemplate> currentActiveTemplate = repository.findActiveTemplate();
        Long previousActiveTemplateId = currentActiveTemplate.map(ScheduleTemplate::templateId).orElse(null);

        // Deactivate all templates
        int deactivatedCount = repository.deactivateAllTemplates();
        log.debug("Deactivated {} templates", deactivatedCount);

        // Activate the requested template
        ScheduleTemplate activatedTemplate = repository.activateTemplate(request.templateId());

        log.info("Successfully activated template {} (previous active: {})",
                activatedTemplate.templateId(), previousActiveTemplateId);

        return responseBuilder.build(activatedTemplate, previousActiveTemplateId);
    }
}
