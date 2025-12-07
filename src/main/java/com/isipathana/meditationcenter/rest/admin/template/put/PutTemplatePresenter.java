package com.isipathana.meditationcenter.rest.admin.template.put;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Presenter for transforming template update data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class PutTemplatePresenter implements PutTemplateResponseBuilder {

    @Override
    public PutTemplateResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap) {

        List<PutTemplateResponse.TemplateActivityResponse> activityResponses =
                templateActivities.stream()
                        .map(ta -> {
                            Activity activity = activityMap.get(ta.activityId());
                            return new PutTemplateResponse.TemplateActivityResponse(
                                    activity.activityId(),
                                    activity.title(),
                                    ta.startTime(),
                                    ta.endTime(),
                                    ta.notes()
                            );
                        })
                        .toList();

        return new PutTemplateResponse(
                template.templateId(),
                template.name(),
                template.description(),
                template.isActive(),
                activityResponses,
                template.updatedAt()
        );
    }
}
