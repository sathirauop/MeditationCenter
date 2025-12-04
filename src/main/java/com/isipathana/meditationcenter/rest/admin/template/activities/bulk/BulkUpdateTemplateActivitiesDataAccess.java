package com.isipathana.meditationcenter.rest.admin.template.activities.bulk;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.Optional;

/**
 * Data access interface for bulk updating template activities.
 *
 * @author Sathira Basnayake
 */
public interface BulkUpdateTemplateActivitiesDataAccess {

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
}
