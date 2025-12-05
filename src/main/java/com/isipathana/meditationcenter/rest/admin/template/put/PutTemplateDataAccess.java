package com.isipathana.meditationcenter.rest.admin.template.put;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.Optional;

/**
 * Data access interface for template update operations.
 *
 * @author Sathira Basnayake
 */
public interface PutTemplateDataAccess {

    /**
     * Finds a template by ID.
     *
     * @param templateId the template ID
     * @return Optional containing the template if found
     */
    Optional<ScheduleTemplate> findTemplateById(Long templateId);

    /**
     * Updates a template's basic information.
     *
     * @param template the template with updated information
     * @return the updated template
     */
    ScheduleTemplate updateTemplate(ScheduleTemplate template);

    /**
     * Deletes all activities for a template.
     *
     * @param templateId the template ID
     * @return number of deleted activities
     */
    int deleteAllTemplateActivities(Long templateId);

    /**
     * Creates a template schedule activity.
     *
     * @param templateActivity the template activity to create
     * @return the created template activity with generated ID
     */
    TemplateScheduleActivity createTemplateActivity(TemplateScheduleActivity templateActivity);

    /**
     * Finds an activity by ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found
     */
    Optional<Activity> findActivityById(Long activityId);
}
