package com.isipathana.meditationcenter.rest.admin.template.activities.put;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating a template activity.
 * Handles business logic for template activity updates.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PutTemplateActivityUseCase implements UseCase<PutTemplateActivityRequest, PutTemplateActivityResponse> {

    private final PutTemplateActivityDataAccess repository;
    private final PutTemplateActivityResponseBuilder responseBuilder;

    @Transactional
    @Override
    public PutTemplateActivityResponse handle(PutTemplateActivityRequest request) {
        log.info("Updating template activity: {}", request.templateActivityId());

        // Verify template activity exists
        TemplateScheduleActivity existingTemplateActivity = repository.findTemplateActivityById(request.templateActivityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Template activity not found with ID: " + request.templateActivityId()));

        // Verify it belongs to the specified template
        if (!existingTemplateActivity.templateId().equals(request.templateId())) {
            throw new ResourceNotFoundException(
                    "Template activity " + request.templateActivityId() +
                    " does not belong to template " + request.templateId());
        }

        // Update template activity
        TemplateScheduleActivity updatedTemplateActivity = TemplateScheduleActivity.builder()
                .id(existingTemplateActivity.id())
                .templateId(existingTemplateActivity.templateId())
                .activityId(existingTemplateActivity.activityId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .notes(request.notes())
                .createdAt(existingTemplateActivity.createdAt())
                .build();

        TemplateScheduleActivity result = repository.updateTemplateActivity(updatedTemplateActivity);

        // Get activity details for response
        Activity activity = repository.findActivityById(result.activityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Activity not found with ID: " + result.activityId()));

        log.info("Successfully updated template activity {}", request.templateActivityId());

        return responseBuilder.build(result, activity);
    }
}
