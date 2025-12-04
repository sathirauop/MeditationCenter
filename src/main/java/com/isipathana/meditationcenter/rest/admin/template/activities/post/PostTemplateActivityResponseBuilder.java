package com.isipathana.meditationcenter.rest.admin.template.activities.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

/**
 * Response builder interface for adding activities to templates.
 *
 * @author Sathira Basnayake
 */
public interface PostTemplateActivityResponseBuilder {

    /**
     * Builds the response for adding an activity to a template.
     *
     * @param templateActivity the created template activity
     * @param activity the activity details
     * @return the formatted response
     */
    PostTemplateActivityResponse build(
            TemplateScheduleActivity templateActivity,
            Activity activity
    );
}
