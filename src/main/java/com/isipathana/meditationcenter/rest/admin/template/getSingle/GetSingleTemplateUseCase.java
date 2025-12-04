package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.architecture.UseCase;
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
 * UseCase for retrieving a single template by ID.
 * Handles business logic for template retrieval with full activity details.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetSingleTemplateUseCase implements UseCase<GetSingleTemplateRequest, GetSingleTemplateResponse> {

    private final GetSingleTemplateDataAccess repository;
    private final GetSingleTemplateResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    @Override
    public GetSingleTemplateResponse handle(GetSingleTemplateRequest request) {
        log.info("Fetching template by ID: {}", request.templateId());

        // Find template
        ScheduleTemplate template = repository.findTemplateById(request.templateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Template not found with ID: " + request.templateId()));

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

        log.info("Successfully retrieved template {} with {} activities",
                template.templateId(), templateActivities.size());

        return responseBuilder.build(template, templateActivities, activityMap);
    }
}
