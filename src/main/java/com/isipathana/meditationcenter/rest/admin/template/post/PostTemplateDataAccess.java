package com.isipathana.meditationcenter.rest.admin.template.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.List;
import java.util.Optional;

/**
 * Data access interface for template creation operations.
 *
 * @author Sathira Basnayake
 */
public interface PostTemplateDataAccess {

    /**
     * Creates a new schedule template.
     *
     * @param template the template to create
     * @return the created template with generated ID
     */
    ScheduleTemplate createTemplate(ScheduleTemplate template);

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
