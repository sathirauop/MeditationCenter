package com.isipathana.meditationcenter.rest.admin.template.activities.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.Optional;

/**
 * Data access interface for adding activities to templates.
 *
 * @author Sathira Basnayake
 */
public interface PostTemplateActivityDataAccess {

    /**
     * Finds a template by ID.
     *
     * @param templateId the template ID
     * @return Optional containing the template if found
     */
    Optional<ScheduleTemplate> findTemplateById(Long templateId);

    /**
     * Finds an activity by ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found
     */
    Optional<Activity> findActivityById(Long activityId);

    /**
     * Creates a template schedule activity.
     *
     * @param templateActivity the template activity to create
     * @return the created template activity with generated ID
     */
    TemplateScheduleActivity createTemplateActivity(TemplateScheduleActivity templateActivity);
}
