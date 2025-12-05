package com.isipathana.meditationcenter.rest.admin.template.getActive;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Presenter for transforming active template data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetActiveTemplatePresenter implements GetActiveTemplateResponseBuilder {

    @Override
    public GetActiveTemplateResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap) {

        List<GetActiveTemplateResponse.TemplateActivityResponse> activityResponses =
                templateActivities.stream()
                        .map(ta -> {
                            Activity activity = activityMap.get(ta.activityId());
                            return new GetActiveTemplateResponse.TemplateActivityResponse(
                                    activity.activityId(),
                                    activity.title(),
                                    ta.startTime(),
                                    ta.endTime(),
                                    ta.notes()
                            );
                        })
                        .toList();

        return new GetActiveTemplateResponse(
                template.templateId(),
                template.name(),
                template.description(),
                template.isActive(),
                activityResponses,
                template.createdAt(),
                template.updatedAt()
        );
    }
}
