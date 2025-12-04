package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.List;
import java.util.Map;

/**
 * Response builder interface for single template retrieval.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleTemplateResponseBuilder {

    /**
     * Builds the response for single template retrieval.
     *
     * @param template the template
     * @param templateActivities the list of template activities
     * @param activityMap map of activity ID to Activity for quick lookup
     * @return the formatted response
     */
    GetSingleTemplateResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap
    );
}
