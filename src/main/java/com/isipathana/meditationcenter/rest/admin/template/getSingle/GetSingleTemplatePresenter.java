package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Presenter for transforming single template data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetSingleTemplatePresenter implements GetSingleTemplateResponseBuilder {

    @Override
    public GetSingleTemplateResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap) {

        List<GetSingleTemplateResponse.TemplateActivityResponse> activityResponses =
                templateActivities.stream()
                        .map(ta -> {
                            Activity activity = activityMap.get(ta.activityId());
                            return new GetSingleTemplateResponse.TemplateActivityResponse(
                                    ta.id(),
                                    activity.activityId(),
                                    activity.title(),
                                    activity.description(),
                                    ta.startTime(),
                                    ta.endTime(),
                                    ta.notes()
                            );
                        })
                        .toList();

        return new GetSingleTemplateResponse(
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
