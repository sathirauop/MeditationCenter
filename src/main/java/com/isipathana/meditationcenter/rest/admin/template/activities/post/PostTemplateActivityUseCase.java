package com.isipathana.meditationcenter.rest.admin.template.activities.post;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for adding an activity to a template.
 * Handles business logic for template activity creation.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostTemplateActivityUseCase implements UseCase<PostTemplateActivityRequest, PostTemplateActivityResponse> {

    private final PostTemplateActivityDataAccess repository;
    private final PostTemplateActivityResponseBuilder responseBuilder;

    @Transactional
    @Override
    public PostTemplateActivityResponse handle(PostTemplateActivityRequest request) {
        log.info("Adding activity {} to template {}", request.activityId(), request.templateId());

        // Verify template exists
        ScheduleTemplate template = repository.findTemplateById(request.templateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Template not found with ID: " + request.templateId()));

        // Verify activity exists
        Activity activity = repository.findActivityById(request.activityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Activity not found with ID: " + request.activityId()));

        // Create template activity
        TemplateScheduleActivity templateActivity = TemplateScheduleActivity.builder()
                .templateId(template.templateId())
                .activityId(activity.activityId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .notes(request.notes())
                .build();

        TemplateScheduleActivity created = repository.createTemplateActivity(templateActivity);

        log.info("Successfully added activity {} to template {}", request.activityId(), request.templateId());

        return responseBuilder.build(created, activity);
    }
}
