package com.isipathana.meditationcenter.rest.admin.template.activities.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.Optional;

/**
 * Data access interface for deleting template activities.
 *
 * @author Sathira Basnayake
 */
public interface DeleteTemplateActivityDataAccess {

    /**
     * Finds a template activity by ID.
     *
     * @param templateActivityId the template activity ID
     * @return Optional containing the template activity if found
     */
    Optional<TemplateScheduleActivity> findTemplateActivityById(Long templateActivityId);

    /**
     * Finds an activity by ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found
     */
    Optional<Activity> findActivityById(Long activityId);

    /**
     * Deletes a template activity by ID.
     *
     * @param templateActivityId the template activity ID
     * @return true if deleted, false otherwise
     */
    boolean deleteTemplateActivity(Long templateActivityId);
}
