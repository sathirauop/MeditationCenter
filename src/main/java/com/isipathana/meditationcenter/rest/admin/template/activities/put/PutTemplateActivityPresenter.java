package com.isipathana.meditationcenter.rest.admin.template.activities.put;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming template activity update data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class PutTemplateActivityPresenter implements PutTemplateActivityResponseBuilder {

    @Override
    public PutTemplateActivityResponse build(
            TemplateScheduleActivity templateActivity,
            Activity activity) {

        return new PutTemplateActivityResponse(
                templateActivity.id(),
                templateActivity.templateId(),
                activity.activityId(),
                activity.title(),
                templateActivity.startTime(),
                templateActivity.endTime(),
                templateActivity.notes(),
                templateActivity.updatedAt()
        );
    }
}
