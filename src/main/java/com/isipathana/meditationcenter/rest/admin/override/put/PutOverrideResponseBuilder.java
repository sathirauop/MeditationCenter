package com.isipathana.meditationcenter.rest.admin.override.put;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Builds PutOverrideResponse from domain objects.
 *
 * @author Sathira Basnayake
 */
@Component
public class PutOverrideResponseBuilder {

    public PutOverrideResponse build(
            ScheduleOverride override,
            List<OverrideActivity> overrideActivities,
            Map<Long, Activity> activityMap
    ) {
        List<PutOverrideResponse.ActivityResponse> activityResponses =
                overrideActivities.stream()
                        .map(oa -> {
                            Activity activity = activityMap.get(oa.activityId());
                            return new PutOverrideResponse.ActivityResponse(
                                    activity.activityId(),
                                    activity.title(),
                                    oa.startTime().toString(),
                                    oa.endTime().toString(),
                                    oa.notes()
                            );
                        })
                        .toList();

        return new PutOverrideResponse(
                override.overrideId(),
                override.overrideDate(),
                activityResponses,
                override.updatedAt()
        );
    }
}
