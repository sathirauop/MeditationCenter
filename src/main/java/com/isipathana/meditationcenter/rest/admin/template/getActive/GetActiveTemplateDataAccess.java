package com.isipathana.meditationcenter.rest.admin.template.getActive;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.List;
import java.util.Optional;

/**
 * Data access interface for retrieving the active template.
 *
 * @author Sathira Basnayake
 */
public interface GetActiveTemplateDataAccess {

    /**
     * Finds the currently active template.
     *
     * @return Optional containing the active template if found
     */
    Optional<ScheduleTemplate> findActiveTemplate();

    /**
     * Gets all activities for a template ordered by start time.
     *
     * @param templateId the template ID
     * @return list of template activities with activity details
     */
    List<TemplateScheduleActivity> getTemplateActivities(Long templateId);

    /**
     * Finds an activity by ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found
     */
    Optional<Activity> findActivityById(Long activityId);
}
