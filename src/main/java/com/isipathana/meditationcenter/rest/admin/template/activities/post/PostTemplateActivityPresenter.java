package com.isipathana.meditationcenter.rest.admin.template.activities.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming template activity creation data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostTemplateActivityPresenter implements PostTemplateActivityResponseBuilder {

    @Override
    public PostTemplateActivityResponse build(
            TemplateScheduleActivity templateActivity,
            Activity activity) {

        return new PostTemplateActivityResponse(
                templateActivity.id(),
                templateActivity.templateId(),
                activity.activityId(),
                activity.title(),
                templateActivity.startTime(),
                templateActivity.endTime(),
                templateActivity.notes(),
                templateActivity.createdAt()
        );
    }
}
