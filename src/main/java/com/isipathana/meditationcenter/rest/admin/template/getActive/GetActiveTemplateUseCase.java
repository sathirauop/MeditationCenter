package com.isipathana.meditationcenter.rest.admin.template.getActive;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UseCase for retrieving the active template.
 * Handles business logic for active template retrieval with activities.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetActiveTemplateUseCase {

    private final GetActiveTemplateDataAccess repository;
    private final GetActiveTemplateResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    public GetActiveTemplateResponse handle() {
        log.info("Fetching active template");

        // Find active template
        ScheduleTemplate template = repository.findActiveTemplate()
                .orElseThrow(() -> new ResourceNotFoundException("No active template found"));

        // Get template activities
        List<TemplateScheduleActivity> templateActivities =
                repository.getTemplateActivities(template.templateId());

        // Load activity details
        Map<Long, Activity> activityMap = new HashMap<>();
        for (TemplateScheduleActivity ta : templateActivities) {
            Activity activity = repository.findActivityById(ta.activityId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Activity not found with ID: " + ta.activityId()));
            activityMap.put(activity.activityId(), activity);
        }

        log.info("Successfully retrieved active template {} with {} activities",
                template.templateId(), templateActivities.size());

        return responseBuilder.build(template, templateActivities, activityMap);
    }
}
