package com.isipathana.meditationcenter.rest.admin.template.activities.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UseCase for deleting template activities.
 * Handles business logic for template activity deletion.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteTemplateActivityUseCase {

    private final DeleteTemplateActivityDataAccess repository;

    @Transactional
    public DeleteTemplateActivityResponse execute(Long templateId, Long templateActivityId) {
        log.info("Starting deletion for template activity: {} in template: {}", templateActivityId, templateId);

        // Verify existence
        Optional<TemplateScheduleActivity> templateActivityOptional =
                repository.findTemplateActivityById(templateActivityId);

        if (templateActivityOptional.isEmpty()) {
            log.warn("Template activity not found: {}", templateActivityId);
            return DeleteTemplateActivityResponse.notFound(templateActivityId, templateId);
        }

        TemplateScheduleActivity templateActivity = templateActivityOptional.get();

        // Verify it belongs to the specified template
        if (!templateActivity.templateId().equals(templateId)) {
            log.warn("Template activity {} does not belong to template {}", templateActivityId, templateId);
            return DeleteTemplateActivityResponse.notFound(templateActivityId, templateId);
        }

        // Get activity details for response
        Activity activity = repository.findActivityById(templateActivity.activityId())
                .orElse(Activity.builder()
                        .activityId(templateActivity.activityId())
                        .title("Unknown Activity")
                        .build());

        // Delete from database
        boolean dbSuccess = repository.deleteTemplateActivity(templateActivityId);

        if (!dbSuccess) {
            log.error("Failed to delete template activity from database");
            throw new RuntimeException("Failed to delete template activity from database");
        }

        log.info("Successfully deleted template activity {}", templateActivityId);
        return DeleteTemplateActivityResponse.success(templateActivityId, templateId, activity.title());
    }
}
