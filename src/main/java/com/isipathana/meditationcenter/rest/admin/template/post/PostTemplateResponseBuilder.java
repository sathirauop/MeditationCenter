package com.isipathana.meditationcenter.rest.admin.template.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.List;
import java.util.Map;

/**
 * Response builder interface for template creation.
 *
 * @author Sathira Basnayake
 */
public interface PostTemplateResponseBuilder {

    /**
     * Builds the response for template creation.
     *
     * @param template the created template
     * @param templateActivities the list of template activities
     * @param activityMap map of activity ID to Activity for quick lookup
     * @return the formatted response
     */
    PostTemplateResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap
    );
}
