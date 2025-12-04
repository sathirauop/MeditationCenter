package com.isipathana.meditationcenter.rest.admin.template.delete;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UseCase for deleting schedule templates.
 * Handles business logic for template deletion.
 * Note: Template activities are automatically deleted via CASCADE constraint.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteTemplateUseCase {

    private final DeleteTemplateDataAccess repository;

    @Transactional
    public DeleteTemplateResponse execute(Long templateId) {
        log.info("Starting deletion for template: {}", templateId);

        // Verify existence
        Optional<ScheduleTemplate> templateOptional = repository.findTemplateById(templateId);

        if (templateOptional.isEmpty()) {
            log.warn("Template not found: {}", templateId);
            return DeleteTemplateResponse.notFound(templateId);
        }

        ScheduleTemplate template = templateOptional.get();

        // Delete from database
        boolean dbSuccess = repository.deleteTemplate(templateId);

        if (!dbSuccess) {
            log.error("Failed to delete template from database");
            throw new RuntimeException("Failed to delete template from database");
        }

        log.info("Successfully deleted template {}", templateId);
        return DeleteTemplateResponse.success(templateId, template.name());
    }
}
