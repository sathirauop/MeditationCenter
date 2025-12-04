package com.isipathana.meditationcenter.rest.admin.template.activities.put;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

/**
 * Response builder interface for updating template activities.
 *
 * @author Sathira Basnayake
 */
public interface PutTemplateActivityResponseBuilder {

    /**
     * Builds the response for updating a template activity.
     *
     * @param templateActivity the updated template activity
     * @param activity the activity details
     * @return the formatted response
     */
    PutTemplateActivityResponse build(
            TemplateScheduleActivity templateActivity,
            Activity activity
    );
}
