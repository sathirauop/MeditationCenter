package com.isipathana.meditationcenter.rest.admin.template.put;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import com.isipathana.meditationcenter.rest.admin.template.post.TemplateActivityDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UseCase for updating schedule templates (full replacement).
 * Handles business logic for template update including activity replacement.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PutTemplateUseCase implements UseCase<PutTemplateRequest, PutTemplateResponse> {

    private final PutTemplateDataAccess repository;
    private final PutTemplateResponseBuilder responseBuilder;

    @Transactional
    @Override
    public PutTemplateResponse handle(PutTemplateRequest request) {
        log.info("Updating template: {}", request.templateId());

        // Verify template exists
        ScheduleTemplate existingTemplate = repository.findTemplateById(request.templateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Template not found with ID: " + request.templateId()));

        // Validate that all activities exist
        Map<Long, Activity> activityMap = validateActivities(request.activities());

        // Update template basic information
        ScheduleTemplate updatedTemplate = ScheduleTemplate.builder()
                .templateId(existingTemplate.templateId())
                .name(request.name())
                .description(request.description())
                .isActive(existingTemplate.isActive()) // Preserve active status
                .createdAt(existingTemplate.createdAt())
                .build();

        updatedTemplate = repository.updateTemplate(updatedTemplate);

        // Delete all existing activities
        int deletedCount = repository.deleteAllTemplateActivities(request.templateId());
        log.debug("Deleted {} existing activities for template {}", deletedCount, request.templateId());

        // Create new activities
        List<TemplateScheduleActivity> templateActivities = new ArrayList<>();
        for (TemplateActivityDto activityDto : request.activities()) {
            TemplateScheduleActivity templateActivity = TemplateScheduleActivity.builder()
                    .templateId(updatedTemplate.templateId())
                    .activityId(activityDto.activityId())
                    .startTime(activityDto.startTime())
                    .endTime(activityDto.endTime())
                    .notes(activityDto.notes())
                    .build();

            TemplateScheduleActivity created = repository.createTemplateActivity(templateActivity);
            templateActivities.add(created);
        }

        log.info("Successfully updated template {} with {} activities",
                updatedTemplate.templateId(), templateActivities.size());

        return responseBuilder.build(updatedTemplate, templateActivities, activityMap);
    }

    /**
     * Validates that all referenced activities exist in the database.
     *
     * @param activities the list of activity DTOs
     * @return map of activity ID to Activity entity
     * @throws ResourceNotFoundException if any activity is not found
     */
    private Map<Long, Activity> validateActivities(List<TemplateActivityDto> activities) {
        Map<Long, Activity> activityMap = new HashMap<>();

        for (TemplateActivityDto activityDto : activities) {
            Activity activity = repository.findActivityById(activityDto.activityId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Activity not found with ID: " + activityDto.activityId()));
            activityMap.put(activity.activityId(), activity);
        }

        return activityMap;
    }
}
