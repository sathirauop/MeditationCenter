package com.isipathana.meditationcenter.rest.admin.template.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Presenter for transforming template creation data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostTemplatePresenter implements PostTemplateResponseBuilder {

    @Override
    public PostTemplateResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap) {

        List<PostTemplateResponse.TemplateActivityResponse> activityResponses =
                templateActivities.stream()
                        .map(ta -> {
                            Activity activity = activityMap.get(ta.activityId());
                            return new PostTemplateResponse.TemplateActivityResponse(
                                    activity.activityId(),
                                    activity.title(),
                                    ta.startTime(),
                                    ta.endTime(),
                                    ta.notes()
                            );
                        })
                        .toList();

        return new PostTemplateResponse(
                template.templateId(),
                template.name(),
                template.description(),
                template.isActive(),
                activityResponses,
                template.createdAt()
        );
    }
}
