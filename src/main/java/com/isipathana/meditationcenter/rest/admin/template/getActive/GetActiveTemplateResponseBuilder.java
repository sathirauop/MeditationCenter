package com.isipathana.meditationcenter.rest.admin.template.getActive;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.List;
import java.util.Map;

/**
 * Response builder interface for active template retrieval.
 *
 * @author Sathira Basnayake
 */
public interface GetActiveTemplateResponseBuilder {

    /**
     * Builds the response for active template retrieval.
     *
     * @param template the active template
     * @param templateActivities the list of template activities
     * @param activityMap map of activity ID to Activity for quick lookup
     * @return the formatted response
     */
    GetActiveTemplateResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap
    );
}
