package com.isipathana.meditationcenter.rest.admin.template.post;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UseCase for creating schedule templates with activities.
 * Handles business logic for template creation including validation and activity linking.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostTemplateUseCase implements UseCase<PostTemplateRequest, PostTemplateResponse> {

    private final PostTemplateDataAccess repository;
    private final PostTemplateResponseBuilder responseBuilder;

    @Transactional
    @Override
    public PostTemplateResponse handle(PostTemplateRequest request) {
        log.info("Creating new template: {}", request.name());

        // Validate that all activities exist
        Map<Long, Activity> activityMap = validateActivities(request.activities());

        // Create the template
        ScheduleTemplate template = ScheduleTemplate.builder()
                .name(request.name())
                .description(request.description())
                .isActive(false) // New templates start as inactive
                .build();

        ScheduleTemplate createdTemplate = repository.createTemplate(template);
        log.debug("Created template with ID: {}", createdTemplate.templateId());

        // Create template activities
        List<TemplateScheduleActivity> templateActivities = new ArrayList<>();
        for (TemplateActivityDto activityDto : request.activities()) {
            TemplateScheduleActivity templateActivity = TemplateScheduleActivity.builder()
                    .templateId(createdTemplate.templateId())
                    .activityId(activityDto.activityId())
                    .startTime(activityDto.startTime())
                    .endTime(activityDto.endTime())
                    .notes(activityDto.notes())
                    .build();

            TemplateScheduleActivity created = repository.createTemplateActivity(templateActivity);
            templateActivities.add(created);
        }

        log.info("Successfully created template {} with {} activities",
                createdTemplate.templateId(), templateActivities.size());

        return responseBuilder.build(createdTemplate, templateActivities, activityMap);
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
