package com.isipathana.meditationcenter.rest.admin.template.activities.put;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.Optional;

/**
 * Data access interface for updating template activities.
 *
 * @author Sathira Basnayake
 */
public interface PutTemplateActivityDataAccess {

    /**
     * Finds a template activity by ID.
     *
     * @param templateActivityId the template activity ID
     * @return Optional containing the template activity if found
     */
    Optional<TemplateScheduleActivity> findTemplateActivityById(Long templateActivityId);

    /**
     * Updates a template activity.
     *
     * @param templateActivity the template activity with updated information
     * @return the updated template activity
     */
    TemplateScheduleActivity updateTemplateActivity(TemplateScheduleActivity templateActivity);

    /**
     * Finds an activity by ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found
     */
    Optional<Activity> findActivityById(Long activityId);
}
