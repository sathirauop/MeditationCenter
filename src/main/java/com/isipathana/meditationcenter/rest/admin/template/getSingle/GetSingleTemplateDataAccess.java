package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.List;
import java.util.Optional;

/**
 * Data access interface for retrieving a single template.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleTemplateDataAccess {

    /**
     * Finds a template by ID.
     *
     * @param templateId the template ID
     * @return Optional containing the template if found
     */
    Optional<ScheduleTemplate> findTemplateById(Long templateId);

    /**
     * Gets all activities for a template ordered by start time.
     *
     * @param templateId the template ID
     * @return list of template activities
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
