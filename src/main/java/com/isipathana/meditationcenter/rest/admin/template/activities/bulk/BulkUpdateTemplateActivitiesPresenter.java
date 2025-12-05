package com.isipathana.meditationcenter.rest.admin.template.activities.bulk;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Presenter for transforming bulk template activities update data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class BulkUpdateTemplateActivitiesPresenter implements BulkUpdateTemplateActivitiesResponseBuilder {

    @Override
    public BulkUpdateTemplateActivitiesResponse build(
            ScheduleTemplate template,
            List<TemplateScheduleActivity> templateActivities,
            Map<Long, Activity> activityMap) {

        List<BulkUpdateTemplateActivitiesResponse.TemplateActivityResponse> activityResponses =
                templateActivities.stream()
                        .map(ta -> {
                            Activity activity = activityMap.get(ta.activityId());
                            return new BulkUpdateTemplateActivitiesResponse.TemplateActivityResponse(
                                    activity.activityId(),
                                    activity.title(),
                                    ta.startTime(),
                                    ta.endTime(),
                                    ta.notes()
                            );
                        })
                        .toList();

        String message = String.format("Successfully updated %d activities for template '%s'",
                templateActivities.size(), template.name());

        return new BulkUpdateTemplateActivitiesResponse(
                template.templateId(),
                template.name(),
                templateActivities.size(),
                activityResponses,
                message
        );
    }
}
