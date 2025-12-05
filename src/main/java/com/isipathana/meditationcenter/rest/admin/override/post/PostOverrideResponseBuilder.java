package com.isipathana.meditationcenter.rest.admin.override.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Builds PostOverrideResponse from domain objects.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostOverrideResponseBuilder {

    public PostOverrideResponse build(
            ScheduleOverride override,
            List<OverrideActivity> overrideActivities,
            Map<Long, Activity> activityMap
    ) {
        List<PostOverrideResponse.OverrideActivityResponse> activityResponses =
                overrideActivities.stream()
                        .map(oa -> {
                            Activity activity = activityMap.get(oa.activityId());
                            return new PostOverrideResponse.OverrideActivityResponse(
                                    activity.activityId(),
                                    activity.title(),
                                    oa.startTime().toString(),
                                    oa.endTime().toString(),
                                    oa.notes()
                            );
                        })
                        .toList();

        return new PostOverrideResponse(
                override.overrideId(),
                override.overrideDate(),
                activityResponses,
                override.createdAt()
        );
    }
}
