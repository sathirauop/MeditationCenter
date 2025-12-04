package com.isipathana.meditationcenter.rest.admin.template.activities.bulk;

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
 * UseCase for bulk updating template activities.
 * Replaces all existing activities with the provided list.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BulkUpdateTemplateActivitiesUseCase implements UseCase<BulkUpdateTemplateActivitiesRequest, BulkUpdateTemplateActivitiesResponse> {

    private final BulkUpdateTemplateActivitiesDataAccess repository;
    private final BulkUpdateTemplateActivitiesResponseBuilder responseBuilder;

    @Transactional
    @Override
    public BulkUpdateTemplateActivitiesResponse handle(BulkUpdateTemplateActivitiesRequest request) {
        log.info("Bulk updating activities for template: {}", request.templateId());

        // Verify template exists
        ScheduleTemplate template = repository.findTemplateById(request.templateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Template not found with ID: " + request.templateId()));

        // Validate that all activities exist
        Map<Long, Activity> activityMap = validateActivities(request.activities());

        // Delete all existing activities
        int deletedCount = repository.deleteAllTemplateActivities(request.templateId());
        log.debug("Deleted {} existing activities for template {}", deletedCount, request.templateId());

        // Create new activities
        List<TemplateScheduleActivity> templateActivities = new ArrayList<>();
        for (TemplateActivityDto activityDto : request.activities()) {
            TemplateScheduleActivity templateActivity = TemplateScheduleActivity.builder()
                    .templateId(template.templateId())
                    .activityId(activityDto.activityId())
                    .startTime(activityDto.startTime())
                    .endTime(activityDto.endTime())
                    .notes(activityDto.notes())
                    .build();

            TemplateScheduleActivity created = repository.createTemplateActivity(templateActivity);
            templateActivities.add(created);
        }

        log.info("Successfully bulk updated {} activities for template {}",
                templateActivities.size(), request.templateId());

        return responseBuilder.build(template, templateActivities, activityMap);
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
