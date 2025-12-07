package com.isipathana.meditationcenter.rest.admin.template.activities.bulk;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

import java.util.List;
import java.util.Map;

/**
 * Response builder interface for bulk updating template activities.
 *
 * @author Sathira Basnayake
 */
public interface BulkUpdateTemplateActivitiesResponseBuilder {

    /**
     * Builds the response for bulk updating template activities.
     *
     * @param template the template
     * @param templateActivities the list of created template activities
     * @param activityMap map of activity ID to Activity for quick lookup
     * @return the formatted response
     */
    BulkUpdateTemplateActivitiesResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap
    );
}
